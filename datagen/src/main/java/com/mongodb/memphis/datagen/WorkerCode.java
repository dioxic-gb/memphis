package com.mongodb.memphis.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bson.BsonBinarySubType;
import org.bson.Document;
import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class WorkerCode implements Runnable {
	CommandLineOptions options;
	Logger logger;
	ValueGen valueGen;
	MongoClient mongoClient;
	MongoDatabase db = null;
	MongoCollection<Document> collection = null;
	MongoCollection<Document> xmlcollection = null;
	RecordTemplate recordTemplate;
	int thread = 0;
	int nshards = 0;
	static Binary blobData = null;

	public WorkerCode(CommandLineOptions options, int i, MongoClient mongoClient, ValueGen valueGen) {
		this.options = options;
		this.valueGen = valueGen;
		this.mongoClient = mongoClient;
		thread = i;
		logger = LoggerFactory.getLogger(WorkerCode.class);
		nshards = (int) Math.round((Double) options.getOption("nShards"));
		String databaseName = (String) options.getOption("database");
		String collectionName = (String) options.getOption("collection");
		List<String> hashIndex = (List<String>) options.getOption("hashIndex");
		List<String> epochIndex = (List<String>) options.getOption("epochIndex");

		db = mongoClient.getDatabase(databaseName);
		collection = db.getCollection(collectionName);

		String xmlcollectionName = (String) options.getOption("xmlcollection");
		if (xmlcollectionName != null) {
			xmlcollection = db.getCollection(xmlcollectionName);
			byte[] data = new byte[3500]; // Sample XML compressed to 3K ish
			Random rng = new Random();
			rng.nextBytes(data);
			blobData = new Binary(BsonBinarySubType.BINARY, data);
			logger.info("XML Blobs enabled");
		}

		recordTemplate = new RecordTemplate((String) options.getOption("template"), hashIndex, epochIndex, valueGen);
	}

	@Override
	public void run() {
		long numRecs = Math.round((Double) options.getOption("numRecs"));
		ArrayList<Document> docs = new ArrayList<>();
		ArrayList<Document> xmldocs = null;
		if (xmlcollection != null) {
			xmldocs = new ArrayList<>();
		}
		int count = 0;
		int batchsize = 1000;

		if (options.getOption("batchsize") instanceof Double) {
			batchsize = (int) Math.round((Double) options.getOption("batchsize"));
		}

		int shard = thread % nshards;
		int r;
		for (r = 0; r < numRecs; r++) {

			// logger.info(t.getTemplateDoc().toJson());
			Document example = recordTemplate.getExampleDocument(shard);

			if (xmlcollection != null) {
				Object msgid = example.get("_id");
				org.bson.Document XML = new Document("_id", msgid);
				XML.append("xml", blobData);
				xmldocs.add(XML);
			}

			docs.add(example);

			count++;
			if (count == batchsize) {
				collection.insertMany(docs);
				docs.clear();
				if (xmlcollection != null) {
					xmlcollection.insertMany(xmldocs);
					xmldocs.clear();
				}
				logger.info("Thread: " + thread + " added " + r);
				shard = (shard + 1) % nshards;
				// logger.info("Writing batch to shard #" + shard);
				count = 0;
			}
		}

		if (!docs.isEmpty()) {
			collection.insertMany(docs);
			docs.clear();
		}
		if (!xmldocs.isEmpty()) {
			xmlcollection.insertMany(xmldocs);
			xmldocs.clear();
		}

		logger.info("Thread: " + thread + " added " + r);
	}
}
