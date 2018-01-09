package com.mongodb.memphis.generator;

import org.bson.BsonInt64;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("long")
public class LongGenerator extends Generator<Long> {

	long min = 0;
	long max = Long.MAX_VALUE;
	Long[] list;

	@Override
	protected Long[] getListValues() {
		return list;
	}

	@Override
	public Long generateValue() {
		return nextLong(min, max);
	}

	@Override
	protected BsonValue toBson(Long value) {
		return new BsonInt64(value);
	}
}
