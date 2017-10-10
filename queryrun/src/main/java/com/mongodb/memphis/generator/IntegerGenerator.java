package com.mongodb.memphis.generator;

import org.bson.BsonInt32;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("integer")
public class IntegerGenerator extends Generator {

	int min = Integer.MIN_VALUE;
	int max = Integer.MAX_VALUE;
	Integer[] list;

	@Override
	protected BsonValue nextValue() {
		int value;
		if (list != null) {
			value = getRandomFromList(list);
		}
		else {
			value = random.nextInt(max - min) + min;
		}
		return new BsonInt32(value);
	}
}
