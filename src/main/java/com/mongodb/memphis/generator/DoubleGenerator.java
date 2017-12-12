package com.mongodb.memphis.generator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.BsonDouble;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("double")
public class DoubleGenerator extends Generator {

	double min = Double.MIN_VALUE;
	double max = Double.MAX_VALUE;
	Double[] list;

	@Override
	protected List<BsonValue> getListValues() {
		return list != null ? Arrays.stream(list).map(BsonDouble::new).collect(Collectors.toList()) : null;
	}

	@Override
	protected BsonValue generateValue() {
		return new BsonDouble(random().nextDouble() * (max - min) + min);
	}

}
