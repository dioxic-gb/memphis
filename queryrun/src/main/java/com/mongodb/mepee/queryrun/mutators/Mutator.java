package com.mongodb.mepee.queryrun.mutators;

import org.bson.BsonDocument;

public interface Mutator {

	void mutate(BsonDocument document);

}