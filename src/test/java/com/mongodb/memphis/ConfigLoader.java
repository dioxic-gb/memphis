package com.mongodb.memphis;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.memphis.config.Root;

public class ConfigLoader {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	@Disabled
	public void loadConfig() throws IOException {
		String configJson = new String(Files.readAllBytes(Paths.get("example-config.json")), StandardCharsets.UTF_8);

		Root root = Root.loadFromJson(configJson);

		logger.info(root.toString());
		assertThat(root).isNotNull();
		// assertEquals(3, config.getIterations());
	}
}
