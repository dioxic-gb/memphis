package com.mongodb.mepee.queryrun;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigParseTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void parseConfig() {
		try {
			Config config = Config.load(new File(getClass().getClassLoader().getResource("config.json").getFile()).getAbsolutePath());
			logger.info(config.toString());
			assertNotNull(config);
			//assertEquals(3, config.getIterations());

		} catch (IOException e) {
			fail();
		}
	}

	@Test
	public void parseQueries() {

	}
}
