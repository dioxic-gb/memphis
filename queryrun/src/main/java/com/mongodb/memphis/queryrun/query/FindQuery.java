package com.mongodb.memphis.queryrun.query;

import java.util.List;

import org.bson.BsonDocument;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import com.mongodb.memphis.queryrun.Config;
import com.mongodb.memphis.queryrun.config.PlaceholderParser;
import com.mongodb.memphis.queryrun.mutators.Mutator;

public class FindQuery extends AbstractQuery {
	private final BsonDocument bsonProjection;

	protected FindQuery(String queryString, Config config, PlaceholderParser parser, List<Mutator> mutators) {
		super("[" + queryString + "]", config, parser, mutators);
		bsonProjection = bsonList.size() > 1 ? bsonList.get(1) : null;
	}

	@Override
	protected <TDocument> MongoIterable<TDocument> executeOperation(MongoCollection<TDocument> collection) {
		FindIterable<TDocument> cursor = collection.find(getMatchDocument());

		if (bsonList.size() > 1) {
			cursor = cursor.projection(bsonList.get(1));
		}

		return cursor;
	}

	@Override
	public String toString() {
		return "find(" + getMatchDocument().toJson() + " , " + bsonProjection + ")";
	}

	@Override
	protected BsonDocument getMatchDocument() {
		return bsonList.get(0);
	}

}
