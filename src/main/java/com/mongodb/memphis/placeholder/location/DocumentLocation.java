package com.mongodb.memphis.placeholder.location;

import org.bson.BsonDocument;
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
public class DocumentLocation extends PlaceholderLocation {
	private BsonDocument document;
	private String key;

	public DocumentLocation(Placeholder placeholder, BsonDocument document, String key) {
		super(placeholder);
		this.document = document;
		this.key = key;
	}

	@Override
	public void apply(BsonValue value) {
		document.put(key, value);
	}

	@Override
	public String toString() {
		return "PlaceHolderLocation [key=" + key + ", value=" + placeholder.toString() + "]";
	}

}