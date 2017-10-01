package com.mongodb.memphis.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.BsonValue;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.memphis.Config;

public abstract class AbstractDbPopulation extends AbstractPopulation {
	private static final char DELIMITER = '|';
	private static final char DOT = '.';
	protected MongoCollection<BsonDocument> collection;
	protected int sampleSize = 10;

	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}
	
	public void initialise(Config config, MongoCollection<BsonDocument> collection) {
		this.collection = collection;
		this.sampleSize = config.getInt("sampleSize");
	}
	
	public void initialise(Config config, MongoClient client, String database, String collection) {
		initialise(config, client.getDatabase(database).getCollection(collection, BsonDocument.class));
	}
	
	protected abstract List<BsonDocument> initialPipeline();
	
	@Override
	protected List<BsonDocument> loadInternal() {
		List<BsonDocument> pipeline = new ArrayList<>();
		pipeline.addAll(initialPipeline());
		pipeline.add(new BsonDocument("$project", calculateProjection(fieldList)));
		pipeline.addAll(calculateUnwind(fieldList));
		pipeline.add(new BsonDocument("$limit", new BsonInt32(sampleSize)));

		logger.debug("population pipeline: {}", pipeline);

		List<BsonDocument> population = new ArrayList<>(sampleSize);
		collection.aggregate(pipeline).into(population);
		postParseSample(population);
		
		return population;
	}

	private void postParseSample(List<BsonDocument> population) {
		for (BsonDocument sample : population) {
			for (String fieldKey : new HashSet<>(sample.keySet())) {
				if (fieldKey.contains(Character.toString(DELIMITER))) {
					BsonValue fieldValue = sample.get(fieldKey);
					sample.remove(fieldKey);
					sample.put(fieldKey.replace(DELIMITER, DOT), fieldValue);
				}
			}
		}
	}

	private List<BsonDocument> calculateUnwind(Collection<String> projectionFields) {
		List<BsonDocument> unwindPipelines = new ArrayList<>();

		for (String field : projectionFields) {
			if (field.contains(Character.toString(DOT))) {
				unwindPipelines.add(new BsonDocument("$unwind", new BsonString("$" + field.replace(DOT, DELIMITER))));
			}
		}

		return unwindPipelines;
	}

	private BsonDocument calculateProjection(Collection<String> projectionFields) {
		BsonDocument fieldsDoc = new BsonDocument();

		for (String field : projectionFields) {
			if (field.contains(Character.toString(DOT))) {
				fieldsDoc.append(field.replace(DOT, DELIMITER), new BsonString("$" + field));
			}
			else {
				fieldsDoc.append(field, new BsonInt32(1));
			}
		}

		// ignore the _id field if it is not a match field
		if (!fieldsDoc.containsKey("_id")) {
			fieldsDoc.append("_id", new BsonInt32(0));
		}

		return fieldsDoc;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [sampleSize=" + sampleSize + "]";
	}
	
}
