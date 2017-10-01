package com.mongodb.memphis.queryrun.config;

import java.util.List;

import org.bson.BsonDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public abstract class Config {

	protected transient Logger logger = LoggerFactory.getLogger(getClass());
	protected transient Config parent;
	private String database;
	private Collection collection;

	public String getDatabase() {
		if (database == null) {
			database = parent.getDatabase();
		}
		return database;
	}

	public Collection getCollection() {
		if (collection == null) {
			collection = parent.getCollection();
		}
		return collection;
	}

	public MongoDatabase getMongoDatabase() {
		return getMongoClient().getDatabase(getDatabase());
	}

	public MongoCollection<BsonDocument> getMongoCollection() {
		return getCollection().getMongoCollection(getMongoDatabase());
	}

	protected void initialise() {}

	protected final void initialiseHierarchy(Config parent) {
		this.parent = parent;
		if (getChildren() != null) {
			for (Config child : getChildren()) {
				child.initialiseHierarchy(this);
				child.initialise();
			}
		}
	}

	public List<? extends Config> getChildren() {
		return null;
	}

	public MongoClient getMongoClient() {
		return parent.getMongoClient();
	}

	public abstract void execute();

}
