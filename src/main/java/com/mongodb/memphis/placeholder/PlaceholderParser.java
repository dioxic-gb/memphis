package com.mongodb.memphis.placeholder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.annotations.ThreadSafe;
import com.mongodb.memphis.placeholder.location.ArrayLocation;
import com.mongodb.memphis.placeholder.location.DocumentLocation;
import com.mongodb.memphis.placeholder.location.PlaceholderLocation;

/**
 * Parses BsonDocuments and binds placeholder locators to fields which have been
 * defined as a placeholder in the config
 *
 * Use one of these per thread.
 *
 * @author Mark Baker-Munton
 */
@ThreadSafe
public class PlaceholderParser {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Pattern pattern = Pattern.compile("\\$\\{(.+)\\}");

	private final Map<String, Placeholder> placeholderMap;

	public PlaceholderParser(Map<String, Placeholder> placeholderMap) {
		this.placeholderMap = placeholderMap;
	}

	public Map<String, Placeholder> getPlaceholderMap() {
		return placeholderMap;
	}

	public Collection<Placeholder> getPlaceholders() {
		return placeholderMap.values();
	}

	public Placeholder getPlaceholder(String key) {
		return placeholderMap.get(key);
	}

	/**
	 * Parse a BsonDcoument and return me a list of locators in priority order
	 *
	 * @param document
	 * @return placeholder locators
	 */
	public List<PlaceholderLocation> parseDocument(BsonDocument document) {
		List<PlaceholderLocation> locations = new ArrayList<>();
		parseDocument(locations, document);
		Collections.sort(locations);
		return locations;
	}

	private void parseDocument(List<PlaceholderLocation> locations, BsonDocument document) {
		for (String key : document.keySet()) {
			BsonValue value = document.get(key);

			if (value.isDocument()) {
				parseDocument(locations, value.asDocument());
			}
			else if (value.isArray()) {
				parseArray(locations, value.asArray());
			}
			else if (value.isString()) {
				BsonString stringValue = value.asString();
				Matcher matcher = pattern.matcher(stringValue.getValue());

				// placeholder value ${xxx}
				if (matcher.find()) {
					String pKey = matcher.group(1);
					String[] attrs = pKey.split(":");

					Placeholder p = placeholderMap.get(attrs[0]);
					if (p != null) {
						locations.add(new DocumentLocation(p, document, key, attrs));
					}
				}
			}
		}
	}

	private void parseArray(List<PlaceholderLocation> locations, BsonArray array) {
		for (int i = 0; i < array.size(); i++) {
			BsonValue value = array.get(i);
			if (value.isDocument()) {
				parseDocument(locations, value.asDocument());
			}
			else if (value.isArray()) {
				parseArray(locations, value.asArray());
			}
			else if (value.isString()) {
				BsonString stringValue = value.asString();
				Matcher matcher = pattern.matcher(stringValue.getValue());

				// placeholder value ${xxx}
				if (matcher.find()) {
					String pKey = matcher.group(1);
					String[] attrs = pKey.split(":");
					Placeholder p = placeholderMap.get(attrs[0]);
					if (p != null) {
						locations.add(new ArrayLocation(p, array, i, attrs));
					}
				}
			}
		}
	}

}
