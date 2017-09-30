package com.mongodb.mepee.queryrun;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.bson.RawBsonDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.mepee.queryrun.WorkerCode.Results;
import com.mongodb.mepee.queryrun.data.PopulationCache;
import com.mongodb.mepee.queryrun.mutators.EpochMutator;
import com.mongodb.mepee.queryrun.mutators.HashMutator;
import com.mongodb.mepee.queryrun.placeholder.PlaceholderParser;
import com.mongodb.mepee.queryrun.query.Query;
import com.mongodb.mepee.queryrun.query.QueryBuilder;

public class WorkerCode implements Callable<Results> {
	private final int threadNum;
	private final Config config;
	private final Query query;
	private final MongoClient client;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Results results = new Results();

	public WorkerCode(MongoClient client, Config config, PopulationCache populationCache, int threadNum) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		this.client = client;
		this.config = config;
		this.threadNum = threadNum;

		query = QueryBuilder.create()
				.setQuery(config.getTemplate(threadNum))
				.setConfig(config)
				.addMutator(new HashMutator(config))
				.addMutator(new EpochMutator(config))
				.setPlaceholderParser(PlaceholderParser.builder().config(config).populationCache(populationCache).build())
				.build();
	}
	
	public Query getQuery() {
		return query;
	}

	@Override
	public Results call() {
		logger.info("Thread {} starting", threadNum);
		results.queryTimes = new long[config.getIterations()];

		MongoDatabase db = client.getDatabase(config.getDatabase());
		MongoCollection<RawBsonDocument> collection = db.getCollection(config.getCollection(), RawBsonDocument.class);

		for (int counter = 0; counter < config.getIterations(); counter++) {
			logger.trace("Thread {} running query iteration {}", threadNum, counter);

			long startTime = System.currentTimeMillis();
			timedBlock(collection);
			results.queryTimes[counter] = System.currentTimeMillis() - startTime;

//			population.next();
		}

		logger.info("Thread {} complete.", threadNum);
		return results;
	}

	private void timedBlock(MongoCollection<RawBsonDocument> collection) {
		try {
			query.execute(collection).forEach(printBlock);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	static class Results {
		long[] queryTimes;
		long recordsReturned;
		long size;

		public long[] getQueryTimes() {
			return queryTimes;
		}

		public long getRecordsReturned() {
			return recordsReturned;
		}
		
		public long totalSize() {
			return size;
		}
	}

	Block<RawBsonDocument> printBlock = new Block<RawBsonDocument>() {
		@Override
		public void apply(final RawBsonDocument document) {
			results.size += document.getByteBuffer().array().length;
			results.recordsReturned++;
			if (logger.isTraceEnabled()) {
				logger.trace("document returned: {}", document.toJson());
			}
		}
	};
}
