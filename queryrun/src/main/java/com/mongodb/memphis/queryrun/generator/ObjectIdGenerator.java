package com.mongodb.memphis.queryrun.generator;

import org.bson.BsonObjectId;
import org.bson.BsonValue;

import com.mongodb.memphis.queryrun.placeholder.Placeholder;

public class ObjectIdGenerator extends AbstractGenerator implements Generator, Placeholder {

	@Override
	public BsonValue getValue() {
		return new BsonObjectId();
	}
}
