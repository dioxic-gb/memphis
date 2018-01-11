package com.mongodb.memphis.placeholder;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaceholderFactoryTest {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void load() {
		PlaceholderFactory factory = PlaceholderFactory.getInstance();
		factory.loadFromFile(null, Paths.get("src/test/resources/test-placeholders.json"));

		logger.info(factory.toString());
	}
}
