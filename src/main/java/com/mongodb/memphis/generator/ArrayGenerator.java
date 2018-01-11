package com.mongodb.memphis.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.bson.BsonArray;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;
import com.mongodb.memphis.placeholder.Placeholder;

@Name("array")
public class ArrayGenerator extends Generator<BsonArray> {

	Integer maxLength;
	Integer minLength = 0;
	BsonArray[] list;
	String source;

	@Override
	protected BsonArray generateValue() {
		Objects.requireNonNull(maxLength, "maxLength cannot be null");
		Objects.requireNonNull(minLength, "minLength cannot be null");
		Objects.requireNonNull(source, "source placeholder cannot be null");

		int size = nextInt(minLength, maxLength + 1);
		Set<BsonValue> values = new HashSet<>(size);

		Placeholder src = placeholderFile.getPlaceholder(source);

		for (int i = 0; i < size; i++) {
			values.add(src.getValue());
		}

		return new BsonArray(new ArrayList<>(values));
	}

	@Override
	protected BsonValue toBson(BsonArray value) {
		return value;
	}

	@Override
	protected BsonArray[] getListValues() {
		return list;
	}

}
