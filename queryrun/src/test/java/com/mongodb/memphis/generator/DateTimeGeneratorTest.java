package com.mongodb.memphis.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.bson.BsonValue;
import org.junit.Test;

public class DateTimeGeneratorTest extends AbstractGeneratorTest {

	@Test
	public void generator() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String lowerBoundString = "2017-01-01T00:00:00";
		String upperBoundString = "2017-01-02T00:00:00";

		LocalDateTime min = LocalDateTime.parse(lowerBoundString);
		LocalDateTime max = LocalDateTime.parse(upperBoundString);
		long minLong = min.toInstant(ZoneOffset.UTC).toEpochMilli();
		long maxLong = max.toInstant(ZoneOffset.UTC).toEpochMilli();

		DateTimeGenerator generator = new DateTimeGenerator();
		generator.min = min;
		generator.max = max;
		generator.initialise();

		for (int i=0; i<100; i++) {
			BsonValue bsonValue = generator.getValue();

			assertThat(bsonValue.isDateTime()).as("is datetime").isTrue();
			assertThat(bsonValue.asDateTime().getValue()).as("generated value in range").isBetween(minLong, maxLong);

			logger.debug("Generated date: {}", Instant.ofEpochMilli(bsonValue.asDateTime().getValue()));
		}
	}
}
