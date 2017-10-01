package com.mongodb.memphis.generator;

import java.util.Random;

import org.bson.BsonString;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("string")
public class StringGenerator extends Generator {

	protected String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	protected int length = 10;

	private transient final Random random = new Random();

	@Override
	public BsonValue nextValue() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(saltChars.charAt(random.nextInt(saltChars.length())));
		}

		return new BsonString(sb.toString());
	}
}
