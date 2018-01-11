package com.mongodb.memphis.placeholder;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.mongodb.memphis.generator.DateTimeGenerator;
import com.mongodb.memphis.generator.IntegerGenerator;
import com.mongodb.memphis.generator.LongGenerator;
import com.mongodb.memphis.generator.StringGenerator;
import com.mongodb.memphis.placeholder.location.PlaceholderLocation;

public class PlaceholderParserTest {

	static Map<String, Placeholder> placeholderMap = Map.ofEntries(
			entry("test1", new IntegerGenerator()),
			entry("test2", new StringGenerator()),
			entry("test3", new LongGenerator()),
			entry("test4", new DateTimeGenerator()));

	@ParameterizedTest
	@ValueSource(strings = {"test1","test2","test3","test4"})
	public void parameterizedPlaceholder(String placeholderKey) {
		BsonDocument doc = constructDocument(placeholderKey + ":input", 5);
		PlaceholderFile parser = new PlaceholderFile(placeholderMap);

		List<PlaceholderLocation> locs = parser.parseDocument(doc);

		assertThat(locs).as("correct number of locations").hasSize(doc.size());
		assertThat(locs).as("correct placeholders").allMatch(e -> e.getPlaceholder().equals(placeholderMap.get(placeholderKey)));
		assertThat(locs).as("correct number of attributes").allMatch(e -> e.getAttributes().length == 2);
		assertThat(locs).as("correct attributes").allMatch(e -> e.getAttributes()[1].equals("input"));
	}

	@ParameterizedTest
	@ValueSource(strings = {"test1","test2","test3","test4"})
	public void basicPlaceholder(String placeholderKey) {
		BsonDocument doc = constructDocument(placeholderKey, 5);
		PlaceholderFile parser = new PlaceholderFile(placeholderMap);

		List<PlaceholderLocation> locs = parser.parseDocument(doc);

		assertThat(locs).as("correct number of locations").hasSize(doc.size());
		assertThat(locs).as("has correct placeholders").allMatch(e -> e.getPlaceholder().equals(placeholderMap.get(placeholderKey)));
	}

	BsonDocument constructDocument(String placeholderKey, int entries) {
		BsonDocument doc = new BsonDocument();

		for (int i=0;i<entries;i++) {
			doc.append("key"+i, new BsonString("${" + placeholderKey + "}"));
		}

		return doc;
	}

}
