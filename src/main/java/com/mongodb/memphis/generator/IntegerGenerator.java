package com.mongodb.memphis.generator;

import java.util.concurrent.atomic.AtomicInteger;

import org.bson.BsonInt32;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("integer")
public class IntegerGenerator extends Generator<Integer> {

	int min = 0;
	int max = Integer.MAX_VALUE;
	Integer[] list;
	boolean increment;
	transient AtomicInteger atomic;

	@Override
	protected Integer[] getListValues() {
		return list;
	}

	@Override
	protected Integer generateValue() {
		return increment ? atomic.getAndAdd(1) : nextInt(min, max);
	}

	@Override
	protected BsonValue toBson(Integer value) {
		return new BsonInt32(value);
	}

	@Override
	public void initialise() {
		super.initialise();
		if (increment) {
			atomic = new AtomicInteger(min);
		}
	}
}
