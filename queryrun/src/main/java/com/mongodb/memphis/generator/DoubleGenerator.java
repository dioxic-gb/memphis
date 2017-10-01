package com.mongodb.memphis.generator;

import org.bson.BsonDouble;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("double")
public class DoubleGenerator extends Generator {

	private double min = Double.MIN_VALUE;
	private double max = Double.MAX_VALUE;
	private Double[] list;

	@Override
	public BsonValue nextValue() {
		double value;
		if (list != null) {
			value = getRandomFromList(list);
		}
		else {
			value = random.nextDouble() * (max - min) + min;
		}
		return new BsonDouble(value);
	}

}
