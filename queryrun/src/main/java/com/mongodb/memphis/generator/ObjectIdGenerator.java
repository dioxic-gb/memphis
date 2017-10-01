package com.mongodb.memphis.generator;

import org.bson.BsonObjectId;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("objectId")
public class ObjectIdGenerator extends Generator {

	@Override
	public BsonValue nextValue() {
		return new BsonObjectId();
	}
}
