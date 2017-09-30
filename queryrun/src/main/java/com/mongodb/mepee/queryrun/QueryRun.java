package com.mongodb.mepee.queryrun;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.LogManager;
import java.util.stream.Collectors;

import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.mepee.queryrun.WorkerCode.Results;
import com.mongodb.mepee.queryrun.data.PopulationCache;
import com.mongodb.mepee.queryrun.query.QueryBuilder;
import com.mongodb.mepee.util.LongStatistics;
import com.mongodb.mepee.util.StringUtils;

import ch.qos.logback.classic.Level;

public class QueryRun {
	static final String version = "0.0.1";
	static Logger logger;
	static Config config;
	static QueryBuilder queryBuilder;

	static void runWorkers() {
		ExecutorService executor = Executors.newFixedThreadPool(config.getThreads());

		logger.info(config.toString());
		
		// set timeout to 1hr
		MongoClientOptions.Builder builder = MongoClientOptions.builder()
										.socketTimeout(60*60*1000)
										.maxConnectionIdleTime(0);

		MongoClient client = new MongoClient(new MongoClientURI("mongodb://" + config.getMongoUri(), builder));

		try {
			PopulationCache populationCache = new PopulationCache(config);
			populationCache.initialiseDbPopulations(client, config.getDatabase(), config.getCollection());

			List<WorkerCode> tasks = new ArrayList<>(config.getThreads());

			for (int i = 0; i < config.getThreads(); i++) {
				tasks.add(new WorkerCode(client, config, populationCache, i));
			}

			// needs to be called after the queries are created
			populationCache.loadData();

			long startTime = System.currentTimeMillis();
			
			// collect the query times once the threads have completed
			List<Results> results = executor.invokeAll(tasks).stream().map(future -> {
				try {
					return future.get();
				} catch (InterruptedException | ExecutionException e) {
					throw new RuntimeException(e);
				}
			}).collect(Collectors.toList());
			
			long totalTime = System.currentTimeMillis() - startTime;

			// load the query times into the stats engine
			LongStatistics stats = new LongStatistics();
			long docsReturned = 0;
			long totalSize = 0;
			for (Results result : results) {
				docsReturned += result.recordsReturned;
				totalSize += result.size;
				for (long time : result.queryTimes) {
					stats.accept(time);
				}
			}

			logger.info("Records returned: {}", docsReturned);
			logger.info("Max query time: {}", StringUtils.prettifyTime(stats.getMax()));
			logger.info("Min query time: {}", StringUtils.prettifyTime(stats.getMin()));
			logger.info("Average query time: {}", StringUtils.prettifyTime((long) stats.getAverage()));
			logger.info("Total query time: {}", StringUtils.prettifyTime(totalTime));
			logger.info("SD of query time: {}", stats.getStandardDeviation());
			logger.info("Total data transferred: {}", StringUtils.prettifySize(totalSize));
			logger.info("Transfer rate: {}", StringUtils.prettifyTransferRate(1000*totalSize/totalTime));

			executor.shutdown();

			logger.info("Query run complete");
		} catch (InterruptedException | ClassNotFoundException | InstantiationException | IllegalAccessException
				| IOException e) {
			throw new RuntimeException(e);
		} finally {
			client.close();
		}
	}

	public static void main(String[] args) {
		LogManager.getLogManager().reset();

		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();

		logger = LoggerFactory.getLogger(QueryRun.class);
		logger.info(version);

		try {
			CommandLineOptions clo = new CommandLineOptions(args);
			config = clo.getConfig();

			if (config.isDebug()) {
				ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory
						.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
				root.setLevel(Level.DEBUG);
			}

			runWorkers();
		} catch (ParseException e) {
			logger.error("Failed to parse command line options", e);
			System.exit(1);
		} catch (RuntimeException e) {
			logger.error("fatal exception", e);
			System.exit(1);
		}
	}

}
