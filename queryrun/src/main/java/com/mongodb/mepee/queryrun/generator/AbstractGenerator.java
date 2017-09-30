package com.mongodb.mepee.queryrun.generator;

import com.mongodb.mepee.queryrun.Config;
import com.mongodb.mepee.queryrun.data.Population;

public abstract class AbstractGenerator implements Generator {

	protected Config config;
	protected Population population;
	protected String queryKey;

	public AbstractGenerator() {

	}

	public void setConfig(Config config) {
		this.config = config;
	}

	@Override
	public void setPopulation(Population population) {
		this.population = population;
	}

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
	
	public void nextBatch() {}

}
