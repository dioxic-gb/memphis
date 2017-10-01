package com.mongodb.memphis.queryrun.generator;

import java.util.Random;

import org.bson.BsonDouble;
import org.bson.BsonValue;

import com.mongodb.memphis.queryrun.placeholder.Placeholder;

public class DoubleGenerator extends AbstractGenerator implements Generator, Placeholder {

	private final Random random = new Random();
	private double lowerBound = Double.MIN_VALUE;
	private double upperBound = Double.MAX_VALUE;

	@Override
	public void setArguements(String[] args) {
		if (args.length == 1) {
			upperBound = Double.parseDouble(args[0]);
		}
		else if (args.length == 2) {
			lowerBound = Double.parseDouble(args[0]);
			upperBound = Double.parseDouble(args[1]);
		}
	}

	@Override
	public BsonValue getValue() {
		return new BsonDouble(random.nextDouble() * (upperBound - lowerBound) + lowerBound);
	}

}
