package com.mongodb.memphis.queryrun.generator;

import com.mongodb.memphis.queryrun.data.Population;
import com.mongodb.memphis.queryrun.placeholder.Placeholder;

public interface Generator extends Placeholder {

	void setArguements(String[] args);

	void setPopulation(Population population);

	void init();

	void setQueryKey(String queryKey);

}