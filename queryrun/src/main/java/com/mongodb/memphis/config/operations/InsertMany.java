package com.mongodb.memphis.config.operations;

import java.util.List;

import org.bson.BsonDocument;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.memphis.DocumentPool;
import com.mongodb.memphis.annotations.Name;
import com.mongodb.memphis.config.Operation;

@Name("find")
public class InsertMany extends Operation {

	private int totalDocuments;
	private int batchSize;
	private InsertManyOptions options;

	public final int getTotalDocuments() {
		return totalDocuments;
	}

	public final int getBatchSize() {
		return batchSize;
	}

	public final InsertManyOptions getOptions() {
		return options;
	}

	@Override
	public int getIterations() {
		return totalDocuments / batchSize / getThreads();
	}

	@Override
	protected void execute(MongoCollection<BsonDocument> collection, List<BsonDocument> documents) {
		collection.insertMany(documents, options);
	}

	@Override
	protected DocumentPool createDocumentPool() {
		return DocumentPool.builder()
				.poolSize(batchSize)
				.templates(templates)
				.build();
	}

}
