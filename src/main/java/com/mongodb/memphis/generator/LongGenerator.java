package com.mongodb.memphis.generator;

import java.util.concurrent.atomic.AtomicLong;

import org.bson.BsonInt64;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("long")
public class LongGenerator extends Generator<Long> {

	long min = 0;
	long max = Long.MAX_VALUE;
	Long[] list;
	boolean increment;
	transient AtomicLong atomic;

	@Override
	protected Long[] getListValues() {
		return list;
	}

	@Override
	public Long generateValue() {
		return increment ? atomic.getAndAdd(1) : nextLong(min, max);
	}

	@Override
	protected BsonValue toBson(Long value) {
		return new BsonInt64(value);
	}

	@Override
	public void initialise() {
		super.initialise();
		if (increment) {
			atomic = new AtomicLong(min);
		}
	}
}
