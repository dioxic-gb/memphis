package com.mongodb.memphis.placeholder.location;

import org.bson.BsonValue;

import com.mongodb.memphis.placeholder.Placeholder;

/**
 * A class to bind data generators to a particular place in an existing
 * document.
 *
 * Used to avoid having to create a new object for every document (we can reuse
 * objects for efficiency).
 *
 * @author Mark Baker-Munton
 */
public abstract class PlaceholderLocation implements Comparable<PlaceholderLocation> {

	protected Placeholder placeholder;

	public PlaceholderLocation(Placeholder placeholder) {
		this.placeholder = placeholder;
	}

	public abstract void apply(BsonValue value);

	public Placeholder getPlaceholder() {
		return placeholder;
	}

	@Override
	public int compareTo(PlaceholderLocation o) {
		return placeholder.compareTo(o.getPlaceholder());
	}

}