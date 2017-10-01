package com.mongodb.memphis.placeholder;

import org.bson.BsonValue;

import com.mongodb.memphis.generator.Generator;
import com.mongodb.memphis.generator.Generator;

public class DataPlaceholder extends Generator implements Name {

	@Override
	public BsonValue getValue() {
		return population.getValue(queryKey);
	}

	@Override
	public void setArguements(String[] args) {
		if (args != null && args.length > 0) {
			queryKey = args[0];
		}
	}

	@Override
	public void init() {
		population.addField(queryKey);
	}
	
}
