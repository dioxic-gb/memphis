package com.mongodb.memphis.generator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.bson.BsonDateTime;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("datetime")
public class DateTimeGenerator extends Generator<Long> {

	private static long NOW = Instant.now().toEpochMilli();

	LocalDateTime min;
	LocalDateTime max;
	private long minLong = 0L; // epoch
	private long maxLong = NOW;

	@Override
	public void initialise() {
		if (min != null) {
			minLong = min.toInstant(ZoneOffset.UTC).toEpochMilli();
		}

		if (max != null) {
			maxLong = max.toInstant(ZoneOffset.UTC).toEpochMilli();
		}
		super.initialise();
	}

	@Override
	protected Long generateValue() {
		return nextLong(minLong, maxLong);
	}

	@Override
	protected BsonValue toBson(Long value) {
		return new BsonDateTime(value);
	}

}
