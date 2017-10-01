package com.mongodb.memphis.queryrun.generator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.mongodb.memphis.queryrun.Config;
import com.mongodb.memphis.queryrun.data.PopulationCache;
import com.mongodb.memphis.queryrun.generator.Generator;
import com.mongodb.memphis.queryrun.generator.GeneratorFactory;

public class DoubleGeneratorTest {

	@Test
	public void generator() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Config config = new Config();
		PopulationCache cache = new PopulationCache(config);

		Generator generator = new GeneratorFactory(cache).createGenerator(null, "@double(12,20)");

		for (int i=0; i<100; i++) {
			assertThat(generator.getValue().isDouble()).as("is double").isTrue();
			assertThat(generator.getValue().asDouble().getValue()).as("generated value in range").isBetween(10d, 25d);
		}
	}
}
