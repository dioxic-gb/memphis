package com.mongodb.mepee.queryrun.generator;

import java.util.Random;

import org.bson.BsonString;
import org.bson.BsonValue;

import com.mongodb.mepee.queryrun.placeholder.Placeholder;

public class StringDateGenerator extends AbstractGenerator implements Generator, Placeholder {

	private final Random random = new Random();

	@Override
	public BsonValue getValue() {

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
