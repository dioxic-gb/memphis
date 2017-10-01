package com.mongodb.memphis.generator;

import java.util.UUID;

import org.bson.BsonString;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("uuid")
public class UUIDGenerator extends Generator {

	@Override
	public BsonValue nextValue() {
		return new BsonString(UUID.randomUUID().toString());
	}
}
