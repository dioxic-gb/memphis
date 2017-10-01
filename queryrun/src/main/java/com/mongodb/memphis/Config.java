package com.mongodb.memphis;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Config {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private String mongoUri;
	private int threads;
	private int iterations;
	private int batchSize;
	private String database;
	private String collection;
	private String placeholderFile;
	private boolean debug;
	private String hashSuffix;
	private String epochSuffix;
	private Map<String, String> populations;
	private List<String> hashIndex;
	private List<String> epochIndex;
	private Map<String, Object> configMap;
	private List<String> templates;

	private void setConfigMap(Map<String, Object> configMap) {
		this.configMap = configMap;
	}

	public Object get(String key) {
		return configMap.get(key);
	}

	public String getString(String key) {
		Object value = get(key);
		if (value == null) {
			logger.warn("config key [{}] not found", key);
			return null;
		}
		return value.toString();
	}

	public Integer getInt(String key) {
		Object value = get(key);
		if (value instanceof Double) {
			return ((Double) value).intValue();
		}
		if (value == null) {
			logger.warn("config key [{}] not found", key);
			return null;
		}
		return Integer.parseInt(value.toString());
	}

	public Map<String, String> getPopulations() {
		return populations;
	}

	public int getThreads() {
		return threads;
	}

	public int getIterations() {
		return iterations;
	}

	public String getPlaceholderFile() {
		return placeholderFile;
	}

	public String getMongoUri() {
		return mongoUri;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public String getDatabase() {
		return database;
	}

	public String getCollection() {
		return collection;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public List<String> getHashIndex() {
		return hashIndex;
	}

	public List<String> getEpochIndex() {
		return epochIndex;
	}

	public void setTemplates(String templateFileOrFolder) throws IOException {
		Path templatePath = Paths.get(templateFileOrFolder);

		if (!Files.exists(templatePath)) {
			throw new IllegalArgumentException(templatePath.toString() + " cannot be found!");
		}
		if (Files.isDirectory(templatePath)) {
			templates = Files.list(templatePath).map(t -> {
				try {
					return new String(Files.readAllBytes(t));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}).collect(Collectors.toList());
		} else {
			templates = new ArrayList<>();
			templates.add(new String(Files.readAllBytes(templatePath)));
		}
	}

	public String getTemplate() {
		return getTemplate(0);
	}

	public String getTemplate(int templateNumber) {
		if (templates == null || templates.isEmpty()) {
			throw new IllegalStateException("no templates have been set!");
		}
		templateNumber = templateNumber % templates.size();
		return templates.get(templateNumber);
	}

	public String getHashSuffix() {
		return hashSuffix;
	}

	public String getEpochSuffix() {
		return epochSuffix;
	}

	public static Config load(String configFile) throws IOException {
		return load(Paths.get(configFile));
	}

	public static Config load(Path configFile) throws IOException {
		String configJson = new String(Files.readAllBytes(configFile), StandardCharsets.UTF_8);
		Gson gson = new Gson();
		Map<String, Object> configMap = gson.fromJson(configJson, new TypeToken<Map<String, Object>>() {
		}.getType());
		Config config = gson.fromJson(configJson, Config.class);
		config.setConfigMap(configMap);
		return config;
	}

	@Override
	public String toString() {
		return "Config " + configMap.toString();
	}

}
