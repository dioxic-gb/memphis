package com.mongodb.memphis.placeholder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.bson.BsonValue;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mongodb.memphis.annotations.Name;
import com.mongodb.memphis.config.adapters.BsonValueTypeAdapter;
import com.mongodb.memphis.config.adapters.LocalDateTimeTypeAdapter;
import com.mongodb.memphis.distribution.IntegerDistributionWrapper;
import com.mongodb.memphis.util.FileUtil;
import com.mongodb.memphis.util.gson.typeadapters.RuntimeTypeAdapterFactory;

public class PlaceholderFactory {
	private final static Logger logger = LoggerFactory.getLogger(PlaceholderFactory.class);

	private static Gson gson;
	private static PlaceholderFactory instance;

	private Map<String, PlaceholderFile> pFileMap;

	static {
		Reflections reflections = new Reflections("com.mongodb.memphis");

		RuntimeTypeAdapterFactory<Placeholder> placeholderAdapterFactory = RuntimeTypeAdapterFactory
				.of(Placeholder.class, "type");

		for (Class<? extends Placeholder> clazz : reflections.getSubTypesOf(Placeholder.class)) {
			Name annotation = clazz.getAnnotation(Name.class);
			if (annotation != null) {
				placeholderAdapterFactory.registerSubtype(clazz, annotation.value());
			}
		}

		RuntimeTypeAdapterFactory<IntegerDistributionWrapper> distributionAdapterFactory = RuntimeTypeAdapterFactory
				.of(IntegerDistributionWrapper.class, "type");

		for (Class<? extends IntegerDistributionWrapper> clazz : reflections.getSubTypesOf(IntegerDistributionWrapper.class)) {
			Name annotation = clazz.getAnnotation(Name.class);
			if (annotation != null) {
				distributionAdapterFactory.registerSubtype(clazz, annotation.value());
			}
		}

		gson = new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapterFactory(placeholderAdapterFactory)
				.registerTypeAdapterFactory(distributionAdapterFactory)
				.registerTypeAdapter(BsonValue.class, new BsonValueTypeAdapter())
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
				.create();
	}

	private PlaceholderFactory() {
		pFileMap = new HashMap<>();
	}

	public static PlaceholderFactory getInstance() {
		if (instance == null) {
			instance = new PlaceholderFactory();
		}
		return instance;
	}

	public PlaceholderFile loadFromJson(String key, String json) {
		// use gson to parse the placeholder json
		Map<String, Placeholder> placeholderMap = gson.fromJson(json, new TypeToken<Map<String, Placeholder>>() {
		}.getType());

		PlaceholderFile pFile = new PlaceholderFile(placeholderMap);
		pFileMap.put(key, pFile);

		// initialise placeholders
		for (Placeholder p : placeholderMap.values()) {
			p.initialise();
			p.setPlaceholderFile(pFile);
		}

		return pFile;
	}

	public PlaceholderFile loadFromFile(String filename) {
		Path path = FileUtil.resolveFile(filename);
		try {
			//PlaceholderParser parser = parserMap.get(filename);
			PlaceholderFile file = pFileMap.get(filename);

			if (file == null) {
				logger.debug("loading placeholder file {}", path);

				file = loadFromJson(filename, new String(Files.readAllBytes(path), StandardCharsets.UTF_8));
			}

			return file;
		}
		catch (IOException e) {
			logger.error("{} - could not parse placeholder file {}", e.getClass().getSimpleName(), path);
			throw new RuntimeException(e);
		}
	}

	public PlaceholderFile getParser(String key) {
		return pFileMap.get(key);
	}

}
