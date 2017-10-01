package com.mongodb.memphis.queryrun.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.annotations.NotThreadSafe;
import com.mongodb.memphis.queryrun.data.PopulationCache;
import com.mongodb.memphis.queryrun.generator.GeneratorFactory;
import com.mongodb.memphis.queryrun.placeholder.ListPlaceholder;
import com.mongodb.memphis.queryrun.placeholder.Placeholder;

/**
 * Parses BsonDocuments and binds placeholder locators to fields which have been
 * defined as a placeholder in the config
 *
 * Use one of these per thread.
 *
 * @author Mark Baker-Munton
 */
@NotThreadSafe
public class PlaceholderParser extends Config {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Pattern pattern = Pattern.compile("\\$\\{(.+)\\}");

	private String placeholderFile;
	private PopulationCache populationCache;

	private transient Map<String, Placeholder> placeholderMap;
	private transient GeneratorFactory generatorFactory;

	public PlaceholderParser(String placeholderFile) {
		this.placeholderFile = placeholderFile;
	}

	public String getPlaceholderFile() {
		return placeholderFile;
	}

	@Override
	protected void initialise() {
		Path placeholderPath = Paths.get(placeholderFile);

		if (!Files.exists(placeholderPath)) {
			throw new IllegalStateException(placeholderPath.toString() + " cannot be found!");
		}
		if (Files.isDirectory(placeholderPath)) {
			throw new IllegalStateException(placeholderPath.toString() + " is a directory!");
		}

		try {
			BsonDocument placeholderConfig = BsonDocument.parse(new String(Files.readAllBytes(placeholderPath), StandardCharsets.UTF_8));
			generatorFactory = new GeneratorFactory(populationCache);
			placeholderMap = new HashMap<>();

			for (String key : placeholderConfig.keySet()) {
				BsonValue bsonValue = placeholderConfig.get(key);

				if (bsonValue.isString()) {
					String value = bsonValue.asString().getValue();
					if (value.startsWith("@") || value.startsWith("#")) {
						placeholderMap.put(key, generatorFactory.createGenerator(key, value));
					}
				}
				else if (bsonValue.isArray()) {
					placeholderMap.put(key, new ListPlaceholder(bsonValue.asArray()));
				}
			}

		}
		catch (IOException e) {
			logger.error("Cannot load placeholder file {}", placeholderFile);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Parse a BsonDcoument and return me a list of locators
	 *
	 * @param document
	 * @return placeholder locators
	 */
	public List<PlaceHolderLocation> parseDocument(BsonDocument document) {
		List<PlaceHolderLocation> locations = new ArrayList<>();
		parseDocument(locations, document);
		return locations;
	}

	private void parseDocument(List<PlaceHolderLocation> locations, BsonDocument document) {
		for (String key : document.keySet()) {
			BsonValue value = document.get(key);

			if (value.isDocument()) {
				parseDocument(locations, value.asDocument());
			}
			else if (value.isString()) {
				BsonString stringValue = value.asString();
				Matcher matcher = pattern.matcher(stringValue.getValue());

				// placeholder value ${xxx}
				if (matcher.find()) {
					String pKey = matcher.group(1);
					locations.add(new PlaceHolderLocation(document, key, placeholderMap.get(pKey)));
				}
			}
		}
	}

	public Collection<Placeholder> getGenerators() {
		return placeholderMap.values();
	}

	@Override
	public void execute() {}

	/**
	 * A class to bind data generators to a particular place in an existing
	 * document.
	 *
	 * Used to avoid having to create a new object for every document (we can reuse
	 * objects for efficiency).
	 *
	 * @author Mark Baker-Munton
	 */
	public static class PlaceHolderLocation {
		BsonDocument document;
		String key;
		Placeholder placeholder;

		public PlaceHolderLocation(BsonDocument document, String key, Placeholder placeholder) {
			this.document = document;
			this.key = key;
			this.placeholder = placeholder;
		}

		public void apply() {
			document.put(key, placeholder.getValue());
		}

		public Placeholder getPlaceholder() {
			return placeholder;
		}

		@Override
		public String toString() {
			return "PlaceHolderLocation [key=" + key + ", value=" + placeholder.toString() + "]";
		}

	}

}
