package com.mongodb.memphis.generator;

import org.bson.BsonObjectId;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("objectId")
public class ObjectIdGenerator extends Generator<BsonObjectId> {

	@Override
	protected BsonObjectId generateValue() {
		return new BsonObjectId();
	}

	@Override
	protected BsonValue toBson(BsonObjectId value) {
		return value;
	}
}
