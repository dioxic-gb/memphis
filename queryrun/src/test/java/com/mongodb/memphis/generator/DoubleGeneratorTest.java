package com.mongodb.memphis.generator;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.BsonValue;
import org.junit.Test;

public class DoubleGeneratorTest extends AbstractGeneratorTest {

	@Test
	public void generator() {
		DoubleGenerator generator = new DoubleGenerator();
		generator.min = 10d;
		generator.max = 500.865d;

		for (int i=0; i<100; i++) {
			BsonValue value = generator.getValue();
			assertThat(value.isDouble()).as("is double").isTrue();
			assertThat(value.asDouble().getValue()).as("generated value in range").isBetween(generator.min, generator.max);
			logger.info("Generated double: {}", value.asDouble().getValue());
		}
	}
}
