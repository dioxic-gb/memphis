package com.mongodb.memphis.queryrun.config;

import org.bson.BsonDocument;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Collection {

	private String name;
	private WriteConcern writeConcern;
	private ReadConcern readConcern;
	private ReadPreference readPreference;

	public final String getName() {
		return name;
	}

	public final WriteConcern getWriteConcern() {
		return writeConcern;
	}

	public final ReadConcern getReadConcern() {
		return readConcern;
	}

	public final ReadPreference getReadPreference() {
		return readPreference;
	}

	public MongoCollection<BsonDocument> getMongoCollection(MongoDatabase mongoDatabase) {
		MongoCollection<BsonDocument> collection = mongoDatabase.getCollection(name, BsonDocument.class);

		if (writeConcern != null) {
			collection.withWriteConcern(writeConcern);
		}
		if (readConcern != null) {
			collection.withReadConcern(readConcern);
		}
		if (readPreference != null) {
			collection.withReadPreference(readPreference);
		}

		return collection;
	}

}
