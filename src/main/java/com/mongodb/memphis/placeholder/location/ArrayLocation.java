package com.mongodb.memphis.placeholder.location;

import org.bson.BsonArray;
import org.bson.BsonValue;

import com.mongodb.memphis.placeholder.Placeholder;

public class ArrayLocation extends PlaceholderLocation {
	private BsonArray array;
	private int index;

	public ArrayLocation(Placeholder placeholder, BsonArray array, int index) {
		super(placeholder);
		this.array = array;
		this.index = index;
	}

	@Override
	public void apply(BsonValue value) {
		array.set(index, value);
	}

	@Override
	public String toString() {
		return "ArrayLocation [index=" + index + ", placeholder=" + placeholder + "]";
	}

}