package com.mongodb.mepee.queryrun.generator;

import org.bson.BsonObjectId;
import org.bson.BsonValue;

import com.mongodb.mepee.queryrun.placeholder.Placeholder;

public class ObjectIdGenerator extends AbstractGenerator implements Generator, Placeholder {

	@Override
	public BsonValue getValue() {
		return new BsonObjectId();
	}
}
