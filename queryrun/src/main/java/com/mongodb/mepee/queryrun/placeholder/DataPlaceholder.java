package com.mongodb.mepee.queryrun.placeholder;

import org.bson.BsonValue;

import com.mongodb.mepee.queryrun.generator.AbstractGenerator;
import com.mongodb.mepee.queryrun.generator.Generator;

public class DataPlaceholder extends AbstractGenerator implements Generator {

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
