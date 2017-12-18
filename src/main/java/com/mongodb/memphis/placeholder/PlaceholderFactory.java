package com.mongodb.memphis.placeholder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mongodb.memphis.annotations.Name;
import com.mongodb.memphis.config.adapters.LocalDateTimeTypeAdapter;
import com.mongodb.memphis.util.gson.typeadapters.RuntimeTypeAdapterFactory;

public class PlaceholderFactory {
	private final static Logger logger = LoggerFactory.getLogger(PlaceholderFactory.class);

	private Map<String, PlaceholderParser> parserMap;

	private static Gson gson;
	private static PlaceholderFactory instance;

	static {
		Reflections reflections = new Reflections("com.mongodb.memphis");

		RuntimeTypeAdapterFactory<Placeholder> typeAdapterFactory = RuntimeTypeAdapterFactory
				.of(Placeholder.class, "type");

		for (Class<? extends Placeholder> clazz : reflections.getSubTypesOf(Placeholder.class)) {
			Name annotation = clazz.getAnnotation(Name.class);
			if (annotation != null) {
				typeAdapterFactory.registerSubtype(clazz, annotation.value());
			}
		}

		gson = new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapterFactory(typeAdapterFactory)
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
				.create();
	}

	private PlaceholderFactory() {
		parserMap = new HashMap<>();
	}

	public static PlaceholderFactory getInstance() {
		if (instance == null) {
			instance = new PlaceholderFactory();
		}
		return instance;
	}

	public void load(String placeholderFile) {
		try {
			PlaceholderParser parser = parserMap.get(placeholderFile);

			if (parser == null) {
				logger.debug("loading placeholder file {}", placeholderFile);
				String json = new String(Files.readAllBytes(Paths.get(placeholderFile)), StandardCharsets.UTF_8);

				// use gson to parse the placeholder json
				Map<String, Placeholder> placeholderMap = gson.fromJson(json, new TypeToken<Map<String, Placeholder>>() {
				}.getType());

				// initialise placeholders
				for (Placeholder placeholder : placeholderMap.values()) {
					placeholder.initialise();
				}

				parserMap.put(placeholderFile, new PlaceholderParser(placeholderMap));
			}
		}
		catch (IOException e) {
			logger.error("{} - could not parse placeholder file {}", e.getClass().getSimpleName(), placeholderFile);
			throw new RuntimeException(e);
		}
	}

	public PlaceholderParser getParser(String placeholderFile) {
		return parserMap.get(placeholderFile);
	}

}
