package com.mongodb.memphis.queryrun.generator;

import com.mongodb.memphis.queryrun.data.Population;

public abstract class AbstractGenerator implements Generator {

	protected Population population;
	protected String queryKey;

	public AbstractGenerator() {

	}

	@Override
	public void setPopulation(Population population) {
		this.population = population;
	}

	@Override
	public void setQueryKey(String queryKey) {
		this.queryKey = queryKey;
	}

	@Override
	public void init() {
	}

	@Override
	public void setArguements(String[] args) {
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName();
		if (population != null) {
			result = result + " [population=" + population + "]";
		}

		return result;
	}

	@Override
	public void nextBatch() {}

}
