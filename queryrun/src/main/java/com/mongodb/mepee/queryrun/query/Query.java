package com.mongodb.mepee.queryrun.query;

import java.util.Collection;

import org.bson.BsonDocument;
import org.bson.BsonValue;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;

public interface Query {

	String getRawQueryString();

	<TDocument> MongoIterable<TDocument> execute(MongoCollection<TDocument> collection);

	Collection<String> getMatchFieldKeys();

	void setMatchFieldValues(BsonDocument matchFields);

	void putMatchField(String key, BsonValue value);

	void removeMatchField(String key);

	BsonValue getMatchFieldValue(String key);

}