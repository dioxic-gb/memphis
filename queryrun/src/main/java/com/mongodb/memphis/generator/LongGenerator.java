package com.mongodb.memphis.generator;

import java.util.Random;

import org.bson.BsonInt64;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("long")
public class LongGenerator extends Generator {

	protected long min = Long.MIN_VALUE;
	protected long max = Long.MAX_VALUE;

	private transient final Random random = new Random();

	@Override
	public BsonValue getValue() {
		Double d = new Double(random.nextDouble() * (max - min));
		return new BsonInt64(d.longValue() + min);
	}
}
