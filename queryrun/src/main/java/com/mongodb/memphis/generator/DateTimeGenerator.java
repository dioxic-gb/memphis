package com.mongodb.memphis.generator;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;

import org.bson.BsonDateTime;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("datetime")
public class DateTimeGenerator extends Generator {

	protected LocalDateTime min = LocalDateTime.MIN;
	protected LocalDateTime max = LocalDateTime.MAX;
	private long minLong;
	private long maxLong;

	private transient final Random random = new Random();

	@Override
	public void initialise() {
		minLong = min.toInstant(ZoneOffset.UTC).toEpochMilli();
		maxLong = max.toInstant(ZoneOffset.UTC).toEpochMilli();
	}

	@Override
	public BsonValue getValue() {
		Double d = new Double(random.nextDouble() * (maxLong - minLong));
		return new BsonDateTime(d.longValue() + minLong);
	}

}
