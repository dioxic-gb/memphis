package com.mongodb.mepee.queryrun.generator;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;

import org.bson.BsonDateTime;
import org.bson.BsonValue;

import com.mongodb.mepee.queryrun.placeholder.Placeholder;

public class DateTimeGenerator extends AbstractGenerator implements Generator, Placeholder {

	private final Random random = new Random();
	
	private long lowerBound;
	private long upperBound;

	@Override
	public void setArguements(String[] args) {
		if (args.length >= 1) {
			lowerBound = LocalDateTime.parse(args[0]).toInstant(ZoneOffset.UTC).toEpochMilli();
		}
		if (args.length >= 2) {
			upperBound = LocalDateTime.parse(args[1]).toInstant(ZoneOffset.UTC).toEpochMilli();
		}
	}

	@Override
	public BsonValue getValue() {
		Double d = new Double(random.nextDouble() * (upperBound - lowerBound));
		return new BsonDateTime(d.longValue() + lowerBound);
	}

}
