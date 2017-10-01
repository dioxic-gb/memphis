package com.mongodb.memphis.generator;

import java.util.Random;

import org.bson.BsonDouble;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("double")
public class DoubleGenerator extends Generator {

	protected double min = Double.MIN_VALUE;
	protected double max = Double.MAX_VALUE;

	private transient final Random random = new Random();

	@Override
	public BsonValue nextValue() {
		return new BsonDouble(random.nextDouble() * (max - min) + min);
	}

}
