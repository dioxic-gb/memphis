package com.mongodb.memphis.config.operations;

import org.bson.BsonDocument;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.memphis.annotations.Name;
import com.mongodb.memphis.config.Operation;
import com.mongodb.memphis.engine.Results;
import com.mongodb.memphis.engine.SingleDocumentPool;

@Name("insertOne")
public class InsertOne extends Operation<SingleDocumentPool> {

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
	protected void execute(MongoCollection<BsonDocument> collection, SingleDocumentPool documentPool, Results results) {
		collection.insertOne(documentPool.getNextDocument(), options);
		results.bytesWritten(documentPool.getCurrentDocumentSize());
		results.docsWritten(1);
	}

	@Override
	protected SingleDocumentPool createDocumentPool() {
		return new SingleDocumentPool(templates);
	}

}
