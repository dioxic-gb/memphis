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
	protected String[] attributes;

	public PlaceholderLocation(Placeholder placeholder, String... attributes) {
		this.placeholder = placeholder;
		this.attributes = attributes;
	}

	protected PlaceholderLocation() {

	}

	public abstract void apply(BsonValue value);

	public Placeholder getPlaceholder() {
		return placeholder;
	}

	public String[] getAttributes() {
		return attributes;
	}

	@Override
	public int compareTo(PlaceholderLocation o) {
		return placeholder.compareTo(o.getPlaceholder());
	}

	public abstract static class Builder<T extends PlaceholderLocation> {
		private Placeholder placeholder;
		private String[] attributes;

		public Builder<T> placeholder(Placeholder placeholder) {
			this.placeholder = placeholder;
			return this;
		}

		public Builder<T> attributes(String[] attributes) {
			this.attributes = attributes;
			return this;
		}

		protected abstract T create();

		public T build() {
			T location = create();
			location.attributes = attributes;
			location.placeholder = placeholder;
			return location;
		}
	}

}