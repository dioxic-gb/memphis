package com.mongodb.memphis.engine;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.RawBsonDocument;
import org.bson.codecs.BsonDocumentCodec;

import com.mongodb.memphis.config.Template;
import com.mongodb.memphis.engine.DocumentPool.Batch;
import com.mongodb.memphis.placeholder.Placeholder;
import com.mongodb.memphis.placeholder.Placeholder.Scope;
import com.mongodb.memphis.placeholder.PlaceholderParser;
import com.mongodb.memphis.placeholder.location.PlaceholderLocation;

public class EngineDocument {
	private Map<Placeholder, BsonValue> placeholderValues;
	private Template template;
	private BsonDocument document;
	private List<PlaceholderLocation> placeholderLocations;
	private Collection<Placeholder> placeholders;

	public EngineDocument(Template template) {
		PlaceholderParser parser = template.getPlaceholderParser();
		this.document = template.getReferenceDocument().clone();
		this.template = template;
		this.placeholderLocations = parser.parseDocument(document);
		Collections.sort(placeholderLocations);
		this.placeholders = parser.getPlaceholders();
	}

	public BsonDocument getDocument() {
		return document;
	}

	public Integer getSize() {
		return template.getDocumentSize();
	}

	public Collection<Placeholder> getPlaceholders() {
		return placeholders;
	}

	private void applyCachedValue(PlaceholderLocation locator) {
		locator.apply(placeholderValues.get(locator.getPlaceholder()));
	}

	public void regenerateValues(Batch batch) {
		// cache values for placeholders in DOCUMENT mode
		for (Placeholder p : placeholders) {
			if (p.getScope() == Scope.DOCUMENT) {
				// lazy load map for efficiency - mostly this won't be used
				if (placeholderValues == null) {
					placeholderValues = new HashMap<>(placeholders.size());
				}
				placeholderValues.put(p, p.getValue(this));
			}
		}

		// apply values to locators
		for (PlaceholderLocation locator : placeholderLocations) {
			Placeholder p = locator.getPlaceholder();
			switch (p.getScope()) {
			case BATCH:
				batch.applyCachedValue(locator);
				break;
			case DOCUMENT:
				this.applyCachedValue(locator);
				break;
			default:
				locator.apply(p.getValue(this));
			}
		}

		// calculate size if not already present
		if (template.getDocumentSize() == null) {
			// we'll cache this value since it will be the same for all
			// other documents from the same template
			template.setDocumentSize(new RawBsonDocument(document, new BsonDocumentCodec()).getByteBuffer().limit());
		}
	}

}