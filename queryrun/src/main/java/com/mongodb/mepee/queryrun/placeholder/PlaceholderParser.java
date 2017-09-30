package com.mongodb.mepee.queryrun.placeholder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
import com.mongodb.mepee.queryrun.Config;
import com.mongodb.mepee.queryrun.data.PopulationCache;
import com.mongodb.mepee.queryrun.generator.GeneratorFactory;

/**
 * Parses BsonDocuments and binds placeholder locators to fields which have been
 * defined as a placeholder in the config
 * 
 * Use one of these per thread.
 * 
 * @author Mark Baker-Munton
 */
@NotThreadSafe
public class PlaceholderParser {
	private final Pattern pattern = Pattern.compile("\\$\\{(.+)\\}");
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private Map<String, Placeholder> placeholderMap;
	private PopulationCache populationCache;
	private GeneratorFactory generatorFactory;
	private Config config;

	private PlaceholderParser() {
	}

	// public PlaceholderParser(Config config) throws IOException {
	// this(config, null);
	// }
	//
	// public PlaceholderParser(Config config, PopulationCache populationCache)
	// throws IOException {
	// String valueJson = new
	// String(Files.readAllBytes(Paths.get(config.getPlaceholderFile())),
	// StandardCharsets.UTF_8);
	// placeholderMap = BsonDocument.parse(valueJson);
	// this.populationCache = populationCache;
	// generatorCache = null;
	// }

	private void initialise() {
		try {
			String valueJson = new String(Files.readAllBytes(Paths.get(config.getPlaceholderFile())), StandardCharsets.UTF_8);
			BsonDocument placeholderConfig = BsonDocument.parse(valueJson);
			generatorFactory = new GeneratorFactory(config, populationCache);
			placeholderMap = new HashMap<>();

			for (String key : placeholderConfig.keySet()) {
				BsonValue bsonValue = placeholderConfig.get(key);

				if (bsonValue.isString()) {
					String value = bsonValue.asString().getValue();
					if (value.startsWith("@") || value.startsWith("#")) {
						placeholderMap.put(key, generatorFactory.createGenerator(key, value));
					}
				} else if (bsonValue.isArray()) {
					placeholderMap.put(key, new ListPlaceholder(bsonValue.asArray()));
				}
			}

		} catch (IOException e) {
			logger.error("Cannot load placeholder file {}", config.getPlaceholderFile());
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
	
	public Collection<Placeholder> getGenerators(){
		return placeholderMap.values();
	}

	private void parseDocument(List<PlaceHolderLocation> locations, BsonDocument document) {
		for (String key : document.keySet()) {
			BsonValue value = document.get(key);

			if (value.isDocument()) {
				parseDocument(locations, value.asDocument());
			} else if (value.isString()) {
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

	public static PlaceHolderParserBuilder builder() {
		return new PlaceHolderParserBuilder();
	}

	public static class PlaceHolderParserBuilder {
		private Config config;
		private PopulationCache populationCache;

		public PlaceHolderParserBuilder config(Config config) {
			this.config = config;
			return this;
		}

		public PlaceHolderParserBuilder populationCache(PopulationCache populationCache) {
			this.populationCache = populationCache;
			return this;
		}

		public PlaceholderParser build() {
			PlaceholderParser parser = new PlaceholderParser();
			parser.config = config;
			parser.populationCache = populationCache;
			parser.initialise();
			return parser;
		}
	}

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
