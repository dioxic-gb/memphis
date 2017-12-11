package com.mongodb.memphis;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.memphis.config.Root;

public class ConfigLoader {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	@Ignore
	public void loadConfig() {
		try {
			String configJson = new String(Files.readAllBytes(Paths.get("example-config.json")), StandardCharsets.UTF_8);

			Root root = Root.loadFromJson(configJson);

			logger.info(root.toString());
			assertNotNull(root);
			// assertEquals(3, config.getIterations());

		}
		catch (IOException e) {
			fail();
		}
	}
}
