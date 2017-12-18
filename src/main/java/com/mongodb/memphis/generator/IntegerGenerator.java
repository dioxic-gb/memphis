package com.mongodb.memphis.generator;

import org.bson.BsonInt32;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("integer")
public class IntegerGenerator extends Generator<Integer> {

	int min = Integer.MIN_VALUE;
	int max = Integer.MAX_VALUE;
	Integer[] list;

	@Override
	protected Integer[] getListValues() {
		return list;
	}

	@Override
	protected Integer generateValue() {
		return nextInt(min, max);
	}

	@Override
	protected BsonValue toBson(Integer value) {
		return new BsonInt32(value);
	}
}
