package com.mongodb.memphis.placeholder;

import org.bson.BsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Placeholder {

	protected transient Logger logger = LoggerFactory.getLogger(getClass());
	private Mode mode = Mode.DEFAULT;

	public void initialise() {
	}

	public abstract BsonValue getValue();

	public void nextBatch(int iteration) {
	}

	public Mode getMode() {
		return mode;
	}

	public enum Mode {
		BATCH,
		DOCUMENT,
		DEFAULT
	}

}
