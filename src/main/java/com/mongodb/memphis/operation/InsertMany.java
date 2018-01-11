package com.mongodb.memphis.operation;

import org.bson.BsonDocument;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.memphis.annotations.Name;
import com.mongodb.memphis.engine.DocumentPool;
import com.mongodb.memphis.engine.Results;

@Name("insertMany")
public class InsertMany extends DataOperation {

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
		if (batchSize > totalDocuments / getThreads()) {
			batchSize = totalDocuments / getThreads();
			logger.debug("reducing batchsize to {}", batchSize);
		}
		return totalDocuments / batchSize / getThreads();
	}

	@Override
	protected void execute(MongoCollection<BsonDocument> collection, DocumentPool documentPool, Results results) {
		if (options != null) {
			collection.insertMany(documentPool.getBatchDocuments(), options);
		}
		else {
			collection.insertMany(documentPool.getBatchDocuments());
		}

		results.bytesWritten(documentPool.getBatchSize());
		results.docsWritten(batchSize);
	}

	@Override
	protected DocumentPool createDocumentPool() {
		return new DocumentPool(templates, batchSize);
	}

}
