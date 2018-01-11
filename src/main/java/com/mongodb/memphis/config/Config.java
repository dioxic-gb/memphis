package com.mongodb.memphis.config;

import java.util.List;

import org.bson.BsonDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.memphis.util.StringUtils;

public abstract class Config {

	protected transient final Logger logger = LoggerFactory.getLogger(getClass());
	protected transient Config parent;
	protected transient int depth;
	protected String name;
	protected String database;
	protected Collection collection;

	public final String getDatabase() {
		if (database == null) {
			database = parent.getDatabase();
		}
		return database;
	}

	public final Collection getCollection() {
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

	protected void initialise() {
	}

	protected final void initialiseHierarchy(Config parent, int depth) {
		this.parent = parent;
		this.depth = depth;
		if (getChildren() != null) {
			for (Config child : getChildren()) {
				child.initialiseHierarchy(this, depth+1);
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

	public String getName() {
		return name != null ? name : "";
	}

	public final void execute() {
		info("{} {} starting", getClass().getSimpleName(), getName());

		long startTime = System.currentTimeMillis();

		executeInternal();

		long totalTime = System.currentTimeMillis() - startTime;

		info("{} {} completed in {}", getClass().getSimpleName(), getName(), StringUtils.prettifyTime(totalTime));
	}

	protected void info(String format, Object... arguements) {
		logger.info(indent(format), arguements);
	}

	private String indent(String s) {
		for (int i=0; i<depth; i++) {
			s = "  " + s;
		}
		return s;
	}

	protected Config getParent() {
		return parent;
	}

	protected Root getRoot() {
		return parent.getRoot();
	}

	protected abstract void executeInternal();

}
