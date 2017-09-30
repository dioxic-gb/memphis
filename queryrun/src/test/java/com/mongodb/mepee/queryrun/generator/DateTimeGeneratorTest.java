package com.mongodb.mepee.queryrun.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.bson.BsonValue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.mepee.queryrun.Config;
import com.mongodb.mepee.queryrun.generator.Generator;
import com.mongodb.mepee.queryrun.generator.GeneratorFactory;

public class DateTimeGeneratorTest {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void generator() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Config config = new Config();
		
		String lowerBoundString = "2017-01-01T00:00:00";
		String upperBoundString = "2017-01-02T00:00:00";
		
		long lowerDt = LocalDateTime.parse(lowerBoundString).toInstant(ZoneOffset.UTC).toEpochMilli();
		long upperDt = LocalDateTime.parse(upperBoundString).toInstant(ZoneOffset.UTC).toEpochMilli();
		
		Generator generator = new GeneratorFactory(config, null).createGenerator(null, "@datetime(" + lowerBoundString + "," + upperBoundString+ ")");

		for (int i=0; i<100; i++) {
			BsonValue bsonValue = generator.getValue();
			
			assertThat(bsonValue.isDateTime()).as("is datetime").isTrue();
			assertThat(bsonValue.asDateTime().getValue()).as("generated value in range").isBetween(lowerDt, upperDt);
			
			logger.info("Generated date:{}", Instant.ofEpochMilli(bsonValue.asDateTime().getValue()));
		}
	}
}
