package com.mongodb.memphis.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bson.BsonValue;

import com.mongodb.memphis.placeholder.Placeholder;

public abstract class Generator extends Placeholder implements Cloneable {

	protected int cardinality;
	private String fieldKey;
	private String cacheKey;

	private transient List<BsonValue> valueCache;
	private transient BsonValue currentValue;

	@Override
	public BsonValue getValue() {
//		if (!batchMode && cacheKey == null) {
//			currentValue = getNextValue();
//		}
		return getNextValue();
	}

	private BsonValue getNextValue() {
		// if we have a list of values or a fixed number of unique values set
		if (valueCache != null) {
			return valueCache.get(random().nextInt(valueCache.size()));
		}
		else {
			return generateValue();
		}
	}

//	public void nextBatch(int iteration) {
//		if (cacheKey != null) {
//			currentValue = DataCache.getValue(cacheKey, fieldKey, Thread.currentThread(), iteration);
//			if (currentValue == null) {
//				throw new IllegalStateException("Could not find fieldKey:" + fieldKey + " in data cache " + cacheKey);
//			}
//		}
//		else if (batchMode) {
//			currentValue = getNextValue();
//		}
//	}

	@Override
	public void initialise() {
		super.initialise();
		List<BsonValue> list = getListValues();
		if (list != null) {
			valueCache = Collections.unmodifiableList(list);
		}
		else if (cardinality > 0) {
			valueCache = new ArrayList<>(cardinality);
			for (int i = 0; i < cardinality; i++) {
				valueCache.add(generateValue());
			}
		}
	}

	protected Random random() {
		return ThreadLocalRandom.current();
	}

	protected abstract BsonValue generateValue();

	protected List<BsonValue> getListValues() {
		return null;
	}

}
