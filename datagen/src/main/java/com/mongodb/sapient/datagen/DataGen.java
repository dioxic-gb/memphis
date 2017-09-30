package com.mongodb.sapient.datagen;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.cli.ParseException;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.File;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;

/**
 * Hello world!
 */
public class DataGen {
    static final String version = "0.0.1";
    static CommandLineOptions options;
    static Logger logger;
    static Integer nThreads = 120;
    static ValueGen valueGen;
    static MongoClient mongoClient;
    static String seedFilesDir;

    static void runWorkers() {
        ExecutorService simexec = Executors
                .newFixedThreadPool(nThreads);
        File dir = new File(seedFilesDir);

        File[] seedFiles = dir.listFiles();
        System.out.println(seedFiles.length);

        int nCount = 0;
        Long start = new Date().getTime();
        for (File seedFile : seedFiles) {
            simexec.execute(new WorkerCode(seedFile, options, nCount++, mongoClient, valueGen));
        }

        simexec.shutdown();

        try {
            simexec.awaitTermination(Long.MAX_VALUE,
                    TimeUnit.SECONDS);
            Long end = new Date().getTime();
            logger.info("Batch Insert Process Time Duration : " + (end - start) + "ms");
            System.out.println("Batch Insert Process Time Duration : " + (end - start) + "ms");
            simexec.shutdown();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());

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
        String templatePath = (String) options.getOption("templatePath");
        mongoClient = new MongoClient(new MongoClientURI(uri));
        seedFilesDir = (String) options.getOption("seedFilesDir");
        // Quick ping to check the connection is good
        mongoClient.getDatabase("admin").runCommand(Document.parse("{ping:1}"));
        nThreads = Integer.parseInt((String)options.getOption("nThreads"));
        valueGen = new ValueGen(templatePath);
        runWorkers();

        System.exit(1);
    }

}
