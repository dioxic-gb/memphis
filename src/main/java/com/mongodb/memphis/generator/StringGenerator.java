package com.mongodb.memphis.generator;

import org.bson.BsonString;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("string")
public class StringGenerator extends Generator<String> {

	String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	int length = 10;
	String[] list;

	@Override
	protected String[] getListValues() {
		return list;
	}

	@Override
	protected String generateValue() {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(saltChars.charAt(nextInt(0, saltChars.length())));
		}
		return sb.toString();
	}

	@Override
	protected BsonValue toBson(String value) {
		return new BsonString(value);
	}
}
