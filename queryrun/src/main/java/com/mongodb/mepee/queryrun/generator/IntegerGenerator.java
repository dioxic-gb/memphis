package com.mongodb.mepee.queryrun.generator;

import java.util.Random;

import org.bson.BsonInt32;
import org.bson.BsonValue;

import com.mongodb.mepee.queryrun.placeholder.Placeholder;

public class IntegerGenerator extends AbstractGenerator implements BatchAwareGenerator, Placeholder {

	private final Random random = new Random();
	private int lowerBound;
	private int upperBound;
	private boolean batchmode = false;
	private BsonInt32 value;

	@Override
	public void setArguements(String[] args) {
		if (args.length >= 1) {
			upperBound = Integer.parseInt(args[0]);
		}
		if (args.length >= 2) {
			lowerBound = Integer.parseInt(args[0]);
			upperBound = Integer.parseInt(args[1]);
		}
		if (args.length >= 3) {
			batchmode = Boolean.parseBoolean(args[2]);
		}
	}

	@Override
	public BsonValue getValue() {
		if (!batchmode || value == null) {
			nextValue();
		}
		return value;
	}

	@Override
	public void nextBatch() {
		nextValue();
	}
	
	private void nextValue() {
		value = new BsonInt32(random.nextInt(upperBound - lowerBound) + lowerBound);
	}
}
