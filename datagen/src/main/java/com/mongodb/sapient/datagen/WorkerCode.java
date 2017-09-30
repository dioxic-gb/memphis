package com.mongodb.sapient.datagen;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateManyModel;
import com.mongodb.client.model.Updates;


public class WorkerCode implements Runnable {
    File seedFile;
    CommandLineOptions options;
    Logger logger;
    ValueGen valueGen;
    MongoClient mongoClient;
    MongoDatabase db = null;
    MongoCollection<Document> collection = null;
    RiskRecordTemplate recordTemplate;
    int thread = 0;
    int nshards;
    LocalDate startDate;
    String numDays;
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
    boolean isBatchUpdate;
    String validFromTimeStamp;
    Long batchSize;

    public WorkerCode(File seedFile, CommandLineOptions options, int i, MongoClient mongoClient, ValueGen valueGen) {
        this.seedFile = seedFile;
        this.options = options;
        this.valueGen = valueGen;
        this.mongoClient = mongoClient;
        this.seedFile = seedFile;
        thread = i;
        logger = LoggerFactory.getLogger(WorkerCode.class);
        //Read file and run loop for each trade id

        this.nshards = Integer.parseInt((String) options.getOption("nShards"));
        String databaseName = (String) options.getOption("database");
        String collectionName = (String) options.getOption("collection");
        String templatePath = (String) options.getOption("templatePath");
        this.isBatchUpdate = (Boolean) options.getOption("batchUpdate");
        this.validFromTimeStamp = (String) options.getOption("validFromTimeStamp");
        batchSize = Long.parseLong((String) options.getOption("batchSize"));

        db = mongoClient.getDatabase(databaseName);
        collection = db.getCollection(collectionName);


        String startDateStr = (String) options.getOption("startDate");
        startDate = LocalDate.parse(startDateStr, format);
        numDays = (String) options.getOption("numDays");
        recordTemplate = new RiskRecordTemplate(templatePath, valueGen);

    }

    public void run() {
        int shard = thread % nshards;
        BufferedReader br = null;
        FileReader fr = null;

        for (int i = 0; i < Integer.parseInt(numDays); i++) {
            String valuationDate = startDate.plusDays(i).toString().replace("-", "");
            try {
                fr = new FileReader(seedFile.getAbsolutePath());
                br = new BufferedReader(fr);
                String sCurrentLine;
                ArrayList<Document> docs = new ArrayList<Document>();
                while ((sCurrentLine = br.readLine()) != null) {
                    String[] vals = sCurrentLine.split(",");
                    docs.addAll(recordTemplate.getExampleDocument(shard, valuationDate, vals[0], vals[1], validFromTimeStamp));
                    if (docs.size() >= batchSize) {
                        // In case of batch insertion , Update the document first then insert it.
                        if (isBatchUpdate) {
                            expireOldRecords(docs);
                        }
                        collection.insertMany(docs);
                        docs.clear();
                        logger.info("Thread: " + thread + " added ");
                        shard = (shard + 1) % nshards;
                    }
                }
                if (docs.size() > 0) {
                    if (isBatchUpdate) {
                        expireOldRecords(docs);
                    }
                    collection.insertMany(docs);
                    docs.clear();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null)
                        br.close();
                    if (fr != null)
                        fr.close();
                } catch (IOException ex) {
                    ex.printStackTrace();

                }
            }
        }
    }


    public void expireOldRecords(ArrayList<Document> docs) {
        Long start = new Date().getTime();
        List<UpdateManyModel<Document>> writeBatch = docs.stream().map(document -> {
            Bson filterCriteria = and(
                    eq("riskSource.tradeId", document.get("riskSource", Document.class).getString("tradeId")),
                    eq("valuationDate", document.get("valuationDate")),
                    //eq("valuationContext.description", document.get("valuationContext", Document.class).getString("description")),
                    eq("measure.name", document.get("measure", Document.class).getString("name")),
                    eq("validTo", 99991231235959L));

            Bson updateCriteria = Updates.set("validTo", document.get("validFrom"));

            return new UpdateManyModel<Document>(filterCriteria, updateCriteria);
        }).collect(Collectors.toList());

        BulkWriteResult result = collection.bulkWrite(writeBatch);
        Long end = new Date().getTime();
        logger.info(result.toString());
        logger.info("Update Duration for thread " + thread + " : " + (end - start) + "ms");
        System.out.println("Update Duration for thread " + thread + " : " + (end - start) + "ms");
    }


}
