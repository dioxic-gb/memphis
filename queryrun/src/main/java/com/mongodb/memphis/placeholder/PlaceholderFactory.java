package com.mongodb.memphis.placeholder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.mongodb.memphis.annotations.Name;
import com.mongodb.memphis.config.adapters.LocalDateTimeTypeAdapter;

public class PlaceholderFactory {
	private final static Logger logger = LoggerFactory.getLogger(PlaceholderFactory.class);

	private String placeholderJson;

	private static Gson gson;

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

	public PlaceholderFactory(String placeholderJson) {
		this.placeholderJson = placeholderJson;
	}

	public static PlaceholderFactory load(String placeholderFile) {
		try {
			return new PlaceholderFactory(new String(Files.readAllBytes(Paths.get(placeholderFile)), StandardCharsets.UTF_8));
		}
		catch (IOException e) {
			logger.error("{} - could not parse placeholder file {}", e.getClass().getSimpleName(), placeholderFile);
			throw new RuntimeException(e);
		}
	}

	public PlaceholderParser create() {
		// use gson to parse the placeholder json
		Map<String, Placeholder> placeholderMap = gson.fromJson(placeholderJson, new TypeToken<Map<String, Placeholder>>() {
		}.getType());

		// initialise placeholders
		for (Placeholder placeholder : placeholderMap.values()) {
			placeholder.initialise();
		}
		return new PlaceholderParser(placeholderMap);
	}

	@Override
	public String toString() {
		return gson.toJson(this);
	}

}
