package com.mongodb.memphis.generator;

import java.util.Random;

import org.bson.BsonValue;

import com.mongodb.memphis.data.Population;
import com.mongodb.memphis.placeholder.Placeholder;

public abstract class Generator extends Placeholder implements Cloneable {

	protected Population population;
	protected String queryKey;
	protected boolean batchMode = false;

	private transient BsonValue value;
	protected transient final Random random = new Random();
	
	public void setPopulation(Population population) {
		this.population = population;
	}

	public void setQueryKey(String queryKey) {
		this.queryKey = queryKey;
	}

	@Override
	public BsonValue getValue() {
		if (!batchMode || value == null) {
			value = nextValue();
		}
		return value;
	}

	@Override
	public void nextBatch() {
		if (batchMode) {
			value = nextValue();
		}
	}

	protected <T> T getRandomFromList(T[] list) {
		return list[random.nextInt(list.length)];
	}

	protected abstract BsonValue nextValue();

}
