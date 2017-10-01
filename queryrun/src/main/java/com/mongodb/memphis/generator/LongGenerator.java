package com.mongodb.memphis.generator;

import org.bson.BsonInt64;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("long")
public class LongGenerator extends Generator {

	private long min = Long.MIN_VALUE;
	private long max = Long.MAX_VALUE;
	private Long[] list;

	@Override
	public BsonValue nextValue() {
		long value;
		if (list != null) {
			value = getRandomFromList(list);
		}
		else {
			value = new Double(random.nextDouble() * (max - min)).longValue() + min;
		}
		return new BsonInt64(value);
	}
}
