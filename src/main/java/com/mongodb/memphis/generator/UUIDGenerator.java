package com.mongodb.memphis.generator;

import java.util.UUID;

import org.bson.BsonString;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("uuid")
public class UUIDGenerator extends Generator<String> {

	@Override
	protected String generateValue() {
		return UUID.randomUUID().toString();
	}

	@Override
	protected BsonValue toBson(String value) {
		return new BsonString(value);
	}
}
