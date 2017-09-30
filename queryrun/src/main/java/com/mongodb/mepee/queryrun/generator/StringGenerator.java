package com.mongodb.mepee.queryrun.generator;

import java.util.Random;

import org.bson.BsonString;
import org.bson.BsonValue;

import com.mongodb.mepee.queryrun.placeholder.Placeholder;

public class StringGenerator extends AbstractGenerator implements Generator, Placeholder {

	private final Random random = new Random();
	private String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private int length = 10;

	@Override
	public void setArguements(String[] args) {
		if (args.length > 0) {
			length = Integer.parseInt(args[0]);
		}
		if (args.length == 2) {
			SALTCHARS = args[1];
		}
	}

	@Override
	public BsonValue getValue() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(SALTCHARS.charAt(random.nextInt(SALTCHARS.length())));
		}

		return new BsonString(sb.toString());
	}
}
