package com.mongodb.memphis.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.bson.BsonValue;

import com.mongodb.memphis.engine.EngineDocument;
import com.mongodb.memphis.placeholder.Placeholder;

public abstract class Generator<T> extends Placeholder {
	protected int cardinality;
	protected Scope scope = Scope.DEFAULT;

	private transient List<BsonValue> valueCache;

	@Override
	public BsonValue getValue() {
		return getNextValue();
	}

	@Override
	public BsonValue getValue(EngineDocument engineDocument) {
		return getValue();
	}

	private BsonValue getNextValue() {
		// if we have a list of values or a fixed number of unique values set
		if (valueCache != null) {
			return valueCache.get(random().nextInt(valueCache.size()));
		}
		else {
			return toBson(generateValue());
		}
	}

	@Override
	public Scope getScope() {
		return scope;
	}

	@Override
	public void initialise() {
		super.initialise();
		if (getListValues() != null) {
			valueCache = Collections.unmodifiableList(Arrays.stream(getListValues()).map(this::toBson).collect(Collectors.toList()));
		}
		else if (cardinality > 0) {
			valueCache = new ArrayList<>(cardinality);
			for (int i = 0; i < cardinality; i++) {
				valueCache.add(toBson(generateValue()));
			}
		}
	}

	private Random random() {
		return ThreadLocalRandom.current();
	}

	protected int nextInt(int min, int max) {
		return random().nextInt(max - min) + min;
	}

	protected long nextLong(long min, long max) {
		return Double.valueOf(random().nextDouble() * (max - min)).longValue() + min;
	}

	protected double nextDouble(double min, double max) {
		return random().nextDouble() * (max - min) + min;
	}

	protected abstract T generateValue();

	protected abstract BsonValue toBson(T value);

	protected T[] getListValues() {
		return null;
	}

}
