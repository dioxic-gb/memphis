package com.mongodb.memphis.generator;

import org.bson.BsonString;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("stringdate")
public class StringDateGenerator extends Generator<String> {

	@Override
	protected String generateValue() {

		int day = nextInt(1,28);
		int year = nextInt(0,5) + 2015;
		int month = nextInt(1,13);
		int hour = nextInt(0,24);
		int minute = nextInt(0,60);
		int second = nextInt(0,60);

		return String.format("%d-%02d-%02dT%02d:%02d:%02dZ",
				year, month, day,
				hour, minute, second);
	}

	@Override
	protected BsonValue toBson(String value) {
		return new BsonString(value);
	}

}
