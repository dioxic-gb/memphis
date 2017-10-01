package com.mongodb.memphis.generator;

import org.bson.BsonString;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("stringdate")
public class StringDateGenerator extends Generator {

	@Override
	public BsonValue nextValue() {

		int day = random.nextInt(28) + 1;
		int year = random.nextInt(5) + 2015;
		int month = random.nextInt(12) + 1;
		int hour = random.nextInt(24);
		int minute = random.nextInt(59) + 1;
		int second = random.nextInt(59) + 1;

		return new BsonString(String.format("%d-%02d-%02dT%02d:%02d:%02dZ",
				year, month, day,
				hour, minute, second));
	}

}
