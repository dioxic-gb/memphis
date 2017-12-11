package com.mongodb.memphis.generator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.BsonInt32;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("integer")
public class IntegerGenerator extends Generator {

	int min = Integer.MIN_VALUE;
	int max = Integer.MAX_VALUE;
	Integer[] list;

	@Override
	protected List<BsonValue> getListValues() {
		return list != null ? Arrays.stream(list).map(x -> new BsonInt32(x)).collect(Collectors.toList()) : null;
	}

	@Override
	protected BsonValue generateValue() {
		return new BsonInt32(random.nextInt(max - min) + min);
	}
}
