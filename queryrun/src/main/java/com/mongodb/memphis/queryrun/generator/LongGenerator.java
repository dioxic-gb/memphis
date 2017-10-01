package com.mongodb.memphis.queryrun.generator;

import java.util.Random;

import org.bson.BsonInt64;
import org.bson.BsonValue;

import com.mongodb.memphis.queryrun.placeholder.Placeholder;

public class LongGenerator extends AbstractGenerator implements Generator, Placeholder {

	private final Random random = new Random();
	private long lowerBound = Long.MIN_VALUE;
	private long upperBound = Long.MAX_VALUE;

	@Override
	public void setArguements(String[] args) {
		if (args.length == 1) {
			upperBound = Long.parseLong(args[0]);
		} else if (args.length == 2) {
			lowerBound = Long.parseLong(args[0]);
			upperBound = Long.parseLong(args[1]);
		}
	}

	@Override
	public BsonValue getValue() {
		Double d = new Double(random.nextDouble() * (upperBound - lowerBound));
		return new BsonInt64(d.longValue() + lowerBound);
	}
}
