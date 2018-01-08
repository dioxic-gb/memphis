package com.mongodb.memphis.engine;

import java.util.Collection;
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
	private Map<String, Placeholder> placeholderMap;

	public EngineDocument(Template template) {
		PlaceholderParser parser = template.getPlaceholderParser();
		this.document = template.getReferenceDocument().clone();
		this.template = template;
		this.placeholderLocations = parser.parseDocument(document);
		this.placeholderMap = parser.getPlaceholderMap();
	}

	public BsonDocument getDocument() {
		return document;
	}

	public BsonValue getFieldValue(String fieldKey) {
		return document.get(fieldKey);
	}

	public Integer getSize() {
		return template.getDocumentSize();
	}

	public Collection<Placeholder> getPlaceholders() {
		return placeholderMap.values();
	}

	public Placeholder getPlaceholder(String key) {
		return placeholderMap.get(key);
	}

	public BsonValue getCachedValue(Placeholder placeholder) {
		return placeholderValues.get(placeholder);
	}

	public void regenerateValues(Batch batch) {
		// cache values for placeholders in DOCUMENT mode
		for (Placeholder p : placeholderMap.values()) {
			if (p.getScope() == Scope.DOCUMENT) {
				// lazy load map for efficiency - mostly this won't be used
				if (placeholderValues == null) {
					placeholderValues = new HashMap<>(placeholderMap.size());
				}
				placeholderValues.put(p, p.getValue());
			}
		}

		// apply values to locators
		for (PlaceholderLocation locator : placeholderLocations) {
			locator.apply(locator.getPlaceholder().getScopedValue(this, batch, locator.getAttributes()));
		}

		// calculate size if not already present
		if (template.getDocumentSize() == null) {
			// we'll cache this value since it will be the same for all
			// other documents from the same template
			template.setDocumentSize(new RawBsonDocument(document, new BsonDocumentCodec()).getByteBuffer().limit());
		}
	}

}