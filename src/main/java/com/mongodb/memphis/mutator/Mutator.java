package com.mongodb.memphis.mutators;

import org.bson.BsonValue;

import com.mongodb.memphis.placeholder.Placeholder;

public abstract class Mutator extends Placeholder {

	protected String input;

	@Override
	public void initialise() {
		priority = 0;
	}

	@Override
	public Mode getMode() {
		return Mode.DEFAULT;
	}

	@Override
	public BsonValue getValue() {
		throw new UnsupportedOperationException("this method is not supported by mutator placeholders");
	}

}
