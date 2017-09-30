package com.mongodb.mepee.queryrun.generator;

import java.util.UUID;

import org.bson.BsonString;
import org.bson.BsonValue;

import com.mongodb.mepee.queryrun.placeholder.Placeholder;

public class UUIDGenerator  extends AbstractGenerator implements Generator, Placeholder {

	@Override
	public BsonValue getValue() {
		return new BsonString(UUID.randomUUID().toString());
	}

}
