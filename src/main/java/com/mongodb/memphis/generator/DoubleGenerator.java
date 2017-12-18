package com.mongodb.memphis.generator;

import org.bson.BsonDouble;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("double")
public class DoubleGenerator extends Generator<Double> {

	double min = Double.MIN_VALUE;
	double max = Double.MAX_VALUE;
	Double[] list;

	@Override
	protected Double[] getListValues() {
		return list;
	}

	@Override
	protected Double generateValue() {
		return nextDouble(min, max);
	}

	@Override
	protected BsonValue toBson(Double value) {
		return new BsonDouble(value);
	}

}
