package com.mongodb.memphis.queryrun.mutators;

import org.bson.BsonDocument;

public interface Mutator {

	void mutate(BsonDocument document);

}