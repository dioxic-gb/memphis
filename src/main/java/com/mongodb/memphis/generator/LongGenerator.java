package com.mongodb.memphis.generator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.BsonInt64;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("long")
public class LongGenerator extends Generator {

	long min = Long.MIN_VALUE;
	long max = Long.MAX_VALUE;
	Long[] list;

	@Override
	protected List<BsonValue> getListValues() {
		return list != null ? Arrays.stream(list).map(BsonInt64::new).collect(Collectors.toList()) : null;
	}

	@Override
	public BsonValue generateValue() {
		return new BsonInt64(new Double(random().nextDouble() * (max - min)).longValue() + min);
	}
}
