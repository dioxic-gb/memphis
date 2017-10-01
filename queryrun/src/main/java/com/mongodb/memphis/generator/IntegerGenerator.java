package com.mongodb.memphis.generator;

import java.util.Random;

import org.bson.BsonInt32;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("integer")
public class IntegerGenerator extends Generator {

	protected int min = Integer.MIN_VALUE;
	protected int max = Integer.MAX_VALUE;
	protected boolean batchmode = false;
	private BsonInt32 value;

	private transient final Random random = new Random();

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
		value = new BsonInt32(random.nextInt(max - min) + min);
	}
}
