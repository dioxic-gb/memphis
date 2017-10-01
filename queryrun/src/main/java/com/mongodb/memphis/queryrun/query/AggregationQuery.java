package com.mongodb.memphis.queryrun.query;

import java.util.List;
import java.util.stream.Collectors;

import org.bson.BsonDocument;
import org.bson.BsonValue;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import com.mongodb.memphis.queryrun.Config;
import com.mongodb.memphis.queryrun.config.PlaceholderParser;
import com.mongodb.memphis.queryrun.mutators.Mutator;

public class AggregationQuery extends AbstractQuery {

	protected AggregationQuery(String query, Config config, PlaceholderParser parser, List<Mutator> mutators) {
		super(query, config, parser, mutators);
	}

	@Override
	protected <TDocument> MongoIterable<TDocument> executeOperation(MongoCollection<TDocument> collection) {
		return collection.aggregate(bsonList);
	}

	public List<BsonDocument> getBsonList() {
		return bsonList;
	}

	@Override
	public String toString() {
		return "aggregate([" + bsonList.stream()
				.map(x -> x.toJson())
				.collect(Collectors.joining(",")) + "])";
	}

	@Override
	protected BsonDocument getMatchDocument() {
		for (BsonDocument doc : bsonList) {
			BsonValue match = doc.get("$match");
			if (match != null && match.isDocument()) {
				return match.asDocument();
			}
		}
		return null;
	}

}
