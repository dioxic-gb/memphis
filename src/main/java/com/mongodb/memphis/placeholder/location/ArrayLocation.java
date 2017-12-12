package com.mongodb.memphis.placeholder.location;

import org.bson.BsonArray;
import org.bson.BsonValue;

import com.mongodb.memphis.placeholder.Placeholder;

public class ArrayLocation implements PlaceholderLocation {
	private BsonArray array;
	private int index;
	private Placeholder placeholder;

	public ArrayLocation(BsonArray array, int index, Placeholder placeholder) {
		this.array = array;
		this.index = index;
		this.placeholder = placeholder;
	}

	@Override
	public void apply() {
		apply(placeholder.getValue());
	}

	@Override
	public void apply(BsonValue value) {
		array.set(index, value);
	}

	@Override
	public Placeholder getPlaceholder() {
		return placeholder;
	}

}