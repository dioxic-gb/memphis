package com.mongodb.memphis.generator;

import java.util.Random;

import org.bson.BsonInt32;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("integer")
public class IntegerGenerator extends Generator {

	protected int min = Integer.MIN_VALUE;
	protected int max = Integer.MAX_VALUE;

	private transient final Random random = new Random();

	@Override
	protected BsonValue nextValue() {
		return new BsonInt32(random.nextInt(max - min) + min);
	}
}
