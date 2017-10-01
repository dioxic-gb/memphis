package com.mongodb.memphis.queryrun.config.operations;

import java.util.List;

import org.bson.BsonDocument;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.memphis.queryrun.ThreadBsonDocumentPool;
import com.mongodb.memphis.queryrun.config.Operation;

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
	protected ThreadBsonDocumentPool getDocumentPool() {
		return ThreadBsonDocumentPool.builder()
				.poolSize(batchSize)
				.templates(templates)
				.build();
	}

}
