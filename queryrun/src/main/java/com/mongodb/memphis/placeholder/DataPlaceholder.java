package com.mongodb.memphis.placeholder;

import org.bson.BsonValue;

import com.mongodb.memphis.generator.Generator;
import com.mongodb.memphis.generator.Generator;

public class DataPlaceholder extends Generator {

	public void setArguements(String[] args) {
		if (args != null && args.length > 0) {
			queryKey = args[0];
		}
	}

	public void init() {
		population.addField(queryKey);
	}

	@Override
	protected BsonValue nextValue() {
		return population.getValue(queryKey);
	}
	
}
