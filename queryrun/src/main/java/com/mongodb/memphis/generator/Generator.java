package com.mongodb.memphis.generator;

import com.mongodb.memphis.data.Population;
import com.mongodb.memphis.placeholder.Placeholder;

public abstract class Generator extends Placeholder implements Cloneable {

	protected Population population;
	protected String queryKey;

	public void setPopulation(Population population) {
		this.population = population;
	}

	public void setQueryKey(String queryKey) {
		this.queryKey = queryKey;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName();
		if (population != null) {
			result = result + " [population=" + population + "]";
		}

		return result;
	}

}
