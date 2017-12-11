package com.mongodb.memphis.mutators;

import org.bson.BsonDocument;

public interface Mutator {

	void mutate(BsonDocument document);

}