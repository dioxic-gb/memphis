package com.mongodb.memphis.data;

import java.util.HashMap;
import java.util.Map;

import org.bson.BsonDocument;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.memphis.Config;

public class PopulationCache {
	private static final String DEFAULT = "default";
	private transient Map<String, Population> cache = new HashMap<>();
	private transient Config config;

	public PopulationCache(Config config)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (config.getPopulations() != null) {
			for (Map.Entry<String, String> eset : config.getPopulations().entrySet()) {
				cache.put(eset.getKey(), (Population) Class.forName(eset.getValue()).newInstance());
			}
		}
		this.config = config;
	}

	public void add(String key, Population population) {
		cache.put(key, population);
	}

	public void addDefault(Population population) {
		cache.put(DEFAULT, population);
	}

	public Population get(String key) {
		if (key == null) {
			key = DEFAULT;
		}
		return cache.get(key);
	}

	public void initialiseDbPopulations(MongoCollection<BsonDocument> collection) {
		for (Population population : cache.values()) {
			if (population instanceof AbstractDbPopulation) {
				((AbstractDbPopulation) population).initialise(config, collection);
			}
		}
	}

	public void loadData() {
		for (Population population : cache.values()) {
			population.loadData();
		}
	}

	public void initialiseDbPopulations(MongoClient client, String database, String collection) {
		initialiseDbPopulations(client.getDatabase(database).getCollection(collection, BsonDocument.class));
	}

}
