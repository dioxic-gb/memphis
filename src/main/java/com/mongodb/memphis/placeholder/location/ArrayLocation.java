package com.mongodb.memphis.placeholder.location;

import org.bson.BsonArray;
import org.bson.BsonValue;

import com.mongodb.memphis.placeholder.Placeholder;

public class ArrayLocation extends PlaceholderLocation {
	private BsonArray array;
	private int index;

	public ArrayLocation(Placeholder placeholder, BsonArray array, int index, String... attributes) {
		super(placeholder, attributes);
		this.array = array;
		this.index = index;
	}

	private ArrayLocation() {

	}

	@Override
	public void apply(BsonValue value) {
		array.set(index, value);
	}

	@Override
	public String toString() {
		return "ArrayLocation [index=" + index + ", placeholder=" + placeholder + "]";
	}

	public static class ArrayLocationBuilder extends Builder<ArrayLocation> {
		private BsonArray array;
		private int index;

		public ArrayLocationBuilder array(BsonArray array) {
			this.array = array;
			return this;
		}

		public ArrayLocationBuilder index(int index) {
			this.index = index;
			return this;
		}

		@Override
		public ArrayLocation build() {
			ArrayLocation loc = build();
			loc.array = array;
			loc.index = index;
			return loc;
		}

		@Override
		protected ArrayLocation create() {
			return new ArrayLocation();
		}

	}

}