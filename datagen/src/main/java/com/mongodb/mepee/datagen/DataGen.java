package com.mongodb.mepee.datagen;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;

import org.apache.commons.cli.ParseException;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * Hello world!
 *
 */
public class DataGen {
	static final String version = "0.0.1";
	static CommandLineOptions options;
	static Logger logger;
	static int nThreads = 4;
	static int runs = 1;
	static ValueGen valueGen;
	static MongoClient mongoClient;

	static void runWorkers() {
		if (options.getOption("nThreads") != null) {
			nThreads = (int) Math.round((Double) options.getOption("nThreads"));
		}
		if (options.getOption("run") != null) {
			nThreads = (int) Math.round((Double) options.getOption("runs"));
		}

		for (int run = 0; run < runs; run++) {
			long startTime = System.currentTimeMillis();
			
			ExecutorService simexec = Executors.newFixedThreadPool(nThreads);
			for (int i = 0; i < nThreads; i++) {
				simexec.execute(new WorkerCode(options, i, mongoClient, valueGen));
			}

			simexec.shutdown();

			try {
				simexec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
				simexec.shutdown();
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
			}
			
			logger.info("Run {} completed in {}", run, System.currentTimeMillis() - startTime);
		}

	}

	public static void main(String[] args) {
		LogManager.getLogManager().reset();

		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
		mongoClient = null;

		logger = LoggerFactory.getLogger(DataGen.class);
		logger.info("Version: " + version);

		try {
			options = new CommandLineOptions(args);
		} catch (ParseException e) {
			logger.error("Failed to parse command line options");
			logger.error(e.getMessage());
			System.exit(1);
		}

		String uri = (String) options.getOption("mongoURI");
		mongoClient = new MongoClient(new MongoClientURI(uri));
		// Quick ping to check the connection is good
		mongoClient.getDatabase("admin").runCommand(Document.parse("{ping:1}"));

		String templatePath = (String) options.getOption("templatePath");
		if (templatePath == null) {
			logger.error("No templatePath in config");
			System.exit(1);
		}
		valueGen = new ValueGen(templatePath);

		// RecordTemplate t = new RecordTemplate("equity-new-trade",valueGen);
		// logger.info(t.getTemplateDoc().toJson());
		// Document example = t.getExampleDocument();
		// logger.info(example.toJson());

		// WorkerCode testWorker = new WorkerCode(options,0,mongoClient,valueGen);
		// testWorker.run();

		runWorkers();

		System.exit(1);
	}

}
