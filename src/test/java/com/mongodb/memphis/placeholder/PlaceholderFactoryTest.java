package com.mongodb.memphis.placeholder;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaceholderFactoryTest {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void load() {
		PlaceholderFactory factory = PlaceholderFactory.getInstance();
		factory.loadFromFile("src/test/resources/test-placeholders.json");

		logger.info(factory.toString());
	}
}
