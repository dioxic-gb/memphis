package com.mongodb.memphis.generator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.BsonString;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("string")
public class StringGenerator extends Generator {

	String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	int length = 10;
	String[] list;

	@Override
	protected List<BsonValue> getListValues() {
		return list != null ? Arrays.stream(list).map(x -> new BsonString(x)).collect(Collectors.toList()) : null;
	}

	@Override
	protected BsonValue generateValue() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(saltChars.charAt(random.nextInt(saltChars.length())));
		}
		return new BsonString(sb.toString());
	}
}
