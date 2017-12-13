package com.mongodb.memphis.placeholder;

import org.bson.BsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.memphis.engine.EngineDocument;

public abstract class Placeholder implements Comparable<Placeholder> {

	protected transient Logger logger = LoggerFactory.getLogger(getClass());
	protected int priority = -1;

	public void initialise() {
	}

	public Scope getScope() {
		return Scope.DEFAULT;
	};

	public abstract BsonValue getValue(EngineDocument engineDocument);

	public abstract BsonValue getValue();

	/**
	 * a higher number will be applied later
	 */
	public int getPriority() {
		return priority;
	}

	public enum Scope {
		BATCH,
		DOCUMENT,
		DEFAULT
	}

	@Override
	public int compareTo(Placeholder o) {
		return Integer.compare(priority, o.getPriority());
	}

}
