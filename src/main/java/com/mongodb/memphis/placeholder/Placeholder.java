package com.mongodb.memphis.placeholder;

import org.bson.BsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Placeholder {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	public void initialise() {
	}

	public abstract BsonValue getValue();

	public void nextBatch(int iteration) {
	}

}
