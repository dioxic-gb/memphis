package com.mongodb.memphis.placeholder.location;

import org.bson.BsonDocument;

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
public class DocumentLocation implements PlaceholderLocation {
	private BsonDocument document;
	private String key;
	private Placeholder placeholder;

	public DocumentLocation(BsonDocument document, String key, Placeholder placeholder) {
		this.document = document;
		this.key = key;
		this.placeholder = placeholder;
	}

	@Override
	public void apply() {
		document.put(key, placeholder.getValue());
	}

	@Override
	public Placeholder getPlaceholder() {
		return placeholder;
	}

	@Override
	public String toString() {
		return "PlaceHolderLocation [key=" + key + ", value=" + placeholder.toString() + "]";
	}

}