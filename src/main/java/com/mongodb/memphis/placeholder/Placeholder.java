package com.mongodb.memphis.placeholder;

import org.bson.BsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.memphis.engine.DocumentPool.Batch;
import com.mongodb.memphis.engine.EngineDocument;

public abstract class Placeholder implements Comparable<Placeholder> {

	protected transient final Logger logger = LoggerFactory.getLogger(getClass());
	protected transient PlaceholderParser placeholderFile;

	protected int priority = -1;

	public abstract void initialise();

	public Scope getScope() {
		return Scope.DEFAULT;
	};

	@Override
	public int compareTo(Placeholder o) {
		return Integer.compare(priority, o.getPriority());
	}

	/**
	 * placeholder get processed in priority order (from lowest to highest).
	 */
	public int getPriority() {
		return priority;
	}

	public void setPlaceholderFile(PlaceholderParser placeholderFile) {
		this.placeholderFile = placeholderFile;
	}

	/**
	 * Get value of placeholder taking into account the scope of the placeholder.
	 * <p>
	 * A document scope will return the cached value at the document-level.
	 * <p>
	 * A batch scope will return the cached value at the batch-level.
	 * @param engineDocument
	 * @param batch
	 * @param attributes
	 * @return
	 */
	public abstract BsonValue getScopedValue(EngineDocument engineDocument, Batch batch, String[] attributes);

	/**
	 * Get the value of the placeholder ignoring any scope.
	 * @return
	 */
	public abstract BsonValue getValue();

	public enum Scope {
		BATCH,
		DOCUMENT,
		DEFAULT
	}

}
