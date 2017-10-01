package com.mongodb.memphis.generator;

import org.bson.BsonString;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("string")
public class StringGenerator extends Generator {

	private String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private int length = 10;
	private String[] list;

	@Override
	public BsonValue nextValue() {
		String value;
		if (list != null) {
			value = getRandomFromList(list);
		}
		else {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < length; i++) {
				sb.append(saltChars.charAt(random.nextInt(saltChars.length())));
			}
			value = sb.toString();
		}

		return new BsonString(value);
	}
}
