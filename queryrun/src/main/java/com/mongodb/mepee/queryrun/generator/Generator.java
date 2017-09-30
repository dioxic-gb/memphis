package com.mongodb.mepee.queryrun.generator;

import com.mongodb.mepee.queryrun.Config;
import com.mongodb.mepee.queryrun.data.Population;
import com.mongodb.mepee.queryrun.placeholder.Placeholder;

public interface Generator extends Placeholder {

	void setArguements(String[] args);

	void setConfig(Config config);

	void setPopulation(Population population);

	void init();

	void setQueryKey(String queryKey);
	
}