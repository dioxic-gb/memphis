package com.mongodb.mepee.queryrun.generator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.mongodb.mepee.queryrun.Config;
import com.mongodb.mepee.queryrun.data.PopulationCache;
import com.mongodb.mepee.queryrun.generator.Generator;
import com.mongodb.mepee.queryrun.generator.GeneratorFactory;

public class IntegerGeneratorTest {

	@Test
	public void generator() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Config config = new Config();
		PopulationCache cache = new PopulationCache(config);
		Generator generator = new GeneratorFactory(config, cache).createGenerator(null, "@integer(12,20)");

		for (int i=0; i<100; i++) {
			assertThat(generator.getValue().isInt32()).as("is integer").isTrue();
			assertThat(generator.getValue().asInt32().getValue()).as("generated value in range").isBetween(10, 25);
		}
	}
}
