package com.mongodb.memphis.placeholder;

import org.bson.BsonValue;

public abstract class Placeholder {

	public void initialise() {
	}

	public abstract BsonValue getValue();

	public void nextBatch() {
	}

}
