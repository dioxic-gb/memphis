package com.mongodb.memphis.config;

import java.util.ArrayList;
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
import com.mongodb.memphis.placeholder.Placeholder;

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
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Pattern pattern = Pattern.compile("\\$\\{(.+)\\}");

	private Map<String, Placeholder> placeholderMap;

	public PlaceholderParser(Map<String, Placeholder> placeholderMap) {
		this.placeholderMap = placeholderMap;
	}

	public java.util.Collection<Placeholder> getPlaceholders() {
		return placeholderMap.values();
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
