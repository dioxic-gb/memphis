package com.mongodb.memphis.generator;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.BsonValue;
import org.junit.Test;

public class IntegerGeneratorTest extends AbstractGeneratorTest {

	@Test
	public void generator() {
		IntegerGenerator generator = new IntegerGenerator();
		generator.min = -100;
		generator.max = 500;

		for (int i=0; i<100; i++) {
			BsonValue value = generator.getValue();
			assertThat(value.isInt32()).as("is integer").isTrue();
			assertThat(value.asInt32().getValue()).as("generated value in range").isBetween(generator.min, generator.max);
			logger.debug("Generated integer: {}", value.asInt32().getValue());
		}
	}
}
