package com.mongodb.memphis.operations;

import org.bson.BsonDocument;
import org.bson.RawBsonDocument;
import org.bson.conversions.Bson;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.memphis.annotations.Name;
import com.mongodb.memphis.engine.Results;
import com.mongodb.memphis.engine.SingleDocumentPool;

@Name("find")
public class Find extends DataOperation<SingleDocumentPool> {

	private int iterations = 1;
	private int limit = -1;
	private int batchSize = -1;
	private Bson sort;

	@Override
	public final int getIterations() {
		return iterations;
	}

	@Override
	protected void execute(MongoCollection<BsonDocument> collection, SingleDocumentPool documentPool, Results results) {
		BsonDocument queryDoc = documentPool.getNextDocument();
		FindIterable<RawBsonDocument> cursor = collection.find(queryDoc, RawBsonDocument.class);

		logger.trace("running {}.find({})", collection.getNamespace(), queryDoc.toJson());

		if (limit != -1) {
			cursor.limit(limit);
		}
		if (batchSize != -1) {
			cursor.batchSize(batchSize);
		}
		if (sort != null) {
			cursor.sort(sort);
		}

		cursor.forEach(new Block<RawBsonDocument>() {
			@Override
			public void apply(final RawBsonDocument document) {
				results.docsRead(1);
				results.bytesRead(document.getByteBuffer().limit());
				if (logger.isTraceEnabled()) {
					logger.trace("document returned: {}", document.toJson());
				}
			}
		});
	}

	@Override
	protected SingleDocumentPool createDocumentPool() {
		return new SingleDocumentPool(templates);
	}

}
