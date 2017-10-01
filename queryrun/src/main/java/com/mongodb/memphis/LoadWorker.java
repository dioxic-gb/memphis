package com.mongodb.memphis;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import org.bson.BsonDocument;
import org.bson.RawBsonDocument;
import org.bson.codecs.BsonDocumentCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.memphis.LoadWorker.Results;
import com.mongodb.memphis.config.PlaceholderParser;

public class LoadWorker implements Callable<Results> {
	private final int threadNum;
	private final Config config;
	private final MongoClient client;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Results results = new Results();
	private final int batchSize;

	public LoadWorker(MongoClient client, Config config, int threadNum)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		this.client = client;
		this.config = config;
		this.threadNum = threadNum;
		this.batchSize = config.getBatchSize();
	}

	@Override
	public Results call() {
		logger.info("Thread {} starting", threadNum);
		results.insertTime = new long[config.getIterations()];
		long rawSize = 0;

		DocumentPool docPool = DocumentPool.builder()
				.poolSize(batchSize)
				.template(BsonDocument.parse(config.getTemplate()))
				.placeholderParser(PlaceholderParser.builder().config(config).build())
				.build();

		MongoDatabase db = client.getDatabase(config.getDatabase());
		MongoCollection<BsonDocument> collection = db.getCollection(config.getCollection(), BsonDocument.class);

		for (int counter = 0; counter < config.getIterations(); counter++) {
			logger.trace("Thread {} running insert iteration {}", threadNum, counter);

			docPool.regenerateValues();

			if (rawSize == 0L) {
				rawSize = getRawSize(docPool.getDocuments().get(0));
			}

			long startTime = System.currentTimeMillis();
			timedBlock(collection, docPool.getDocuments());
			results.insertTime[counter] = System.currentTimeMillis() - startTime;
			results.size += rawSize * batchSize;
			results.recordCount += batchSize;
		}

		logger.info("Thread {} complete.", threadNum);
		return results;
	}

	private long getRawSize(BsonDocument document) {
		return new RawBsonDocument(document, new BsonDocumentCodec()).getByteBuffer().limit();
	}

	private void timedBlock(MongoCollection<BsonDocument> collection, List<BsonDocument> documents) {
		try {
			InsertManyOptions insertOptions = new InsertManyOptions();
			insertOptions.ordered(false);
			insertOptions.bypassDocumentValidation(true);
			collection.insertMany(documents, insertOptions);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	static class Results {
		long[] insertTime;
		long size = 0;
		long recordCount;

		public long[] getQueryTimes() {
			return insertTime;
		}

		public long totalSize() {
			return size;
		}

		public long recordCount() {
			return recordCount;
		}
	}

	Block<RawBsonDocument> printBlock = new Block<RawBsonDocument>() {
		@Override
		public void apply(final RawBsonDocument document) {
			results.size += document.getByteBuffer().array().length;
			if (logger.isTraceEnabled()) {
				logger.trace("document returned: {}", document.toJson());
			}
		}
	};
}
