package com.mongodb.memphis.operations;

import org.bson.BsonDocument;
import org.bson.RawBsonDocument;
import org.bson.conversions.Bson;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.memphis.annotations.Name;
import com.mongodb.memphis.engine.DocumentPool;
import com.mongodb.memphis.engine.Results;

@Name("find")
public class Find extends DataOperation {

	private int iterations = 1;
	private int limit = -1;
	private int batchSize = -1;
	private Bson sort;

	@Override
	public final int getIterations() {
		return iterations;
	}

	@Override
	protected void execute(MongoCollection<BsonDocument> collection, DocumentPool documentPool, Results results) {
		BsonDocument queryDoc = documentPool.getDocument();
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
	protected DocumentPool createDocumentPool() {
		return new DocumentPool(templates, 1);
	}

}
