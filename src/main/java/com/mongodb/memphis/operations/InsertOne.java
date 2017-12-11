package com.mongodb.memphis.operations;

import org.bson.BsonDocument;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.memphis.annotations.Name;
import com.mongodb.memphis.engine.DocumentPool;
import com.mongodb.memphis.engine.Results;

@Name("insertOne")
public class InsertOne extends DataOperation {

	private int totalDocuments;
	private InsertOneOptions options;

	public final int getTotalDocuments() {
		return totalDocuments;
	}

	public final InsertOneOptions getOptions() {
		return options;
	}

	@Override
	public int getIterations() {
		return totalDocuments / getThreads();
	}

	@Override
	protected void execute(MongoCollection<BsonDocument> collection, DocumentPool documentPool, Results results) {
		collection.insertOne(documentPool.getDocument(), options);
		results.bytesWritten(documentPool.getBatchSize());
		results.docsWritten(1);
	}

	@Override
	protected DocumentPool createDocumentPool() {
		return new DocumentPool(templates, 1);
	}

}
