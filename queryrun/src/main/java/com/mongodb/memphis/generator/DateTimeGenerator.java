package com.mongodb.memphis.generator;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.bson.BsonDateTime;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("datetime")
public class DateTimeGenerator extends Generator {

	LocalDateTime min = LocalDateTime.MIN;
	LocalDateTime max = LocalDateTime.MAX;
	private long minLong;
	private long maxLong;

	@Override
	public void initialise() {
		minLong = min.toInstant(ZoneOffset.UTC).toEpochMilli();
		maxLong = max.toInstant(ZoneOffset.UTC).toEpochMilli();
	}

	@Override
	public BsonValue nextValue() {
		Double d = new Double(random.nextDouble() * (maxLong - minLong));
		return new BsonDateTime(d.longValue() + minLong);
	}

}
