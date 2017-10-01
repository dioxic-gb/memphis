package com.mongodb.memphis.placeholder;

import java.util.ArrayList;
import java.util.List;

import org.bson.BsonArray;
import org.bson.BsonValue;

import com.mongodb.memphis.generator.Generator;
import com.mongodb.memphis.generator.Generator;

public class DataArrayPlaceholder extends Generator implements Name {

	private int arraySize;

	@Override
	public BsonValue getValue() {
		List<BsonValue> bsonList = new ArrayList<>();

		for (int i = 0; i < arraySize; i++) {
			bsonList.add(population.getValue(queryKey));
			population.next();
		}

		return new BsonArray(bsonList);
	}

	@Override
	public void setArguements(String[] args) {
		if (args != null && args.length == 1) {
			arraySize = Integer.parseInt(args[0]);
		} else if (args != null && args.length == 2) {
			arraySize = Integer.parseInt(args[0]);
			queryKey = args[1];
		}
	}

	@Override
	public void init() {
		population.addField(queryKey);
	}

}
