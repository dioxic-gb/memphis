package com.mongodb.memphis.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bson.BsonDocument;

import com.mongodb.memphis.config.Template;
import com.mongodb.memphis.placeholder.Placeholder;
import com.mongodb.memphis.placeholder.PlaceholderParser;
import com.mongodb.memphis.placeholder.location.PlaceholderLocation;

public class AbstractDocumentPool {

	protected int poolSize;
	protected List<Template> templates;
	protected List<BsonDocument> documents;
	protected List<PlaceholderLocation> placeholderLocations;
	protected Collection<Placeholder> placeholders;
	protected BsonDocument[] templateExamples;
	protected long averageDocumentSize = -1;

	public AbstractDocumentPool(List<Template> templates, int poolSize) {
		this.templates = templates;
		this.poolSize = poolSize;
		initialise();
	}

	private void initialise() {
		documents = new ArrayList<>(poolSize);
		placeholderLocations = new ArrayList<>(poolSize);
		placeholders = new ArrayList<>();
		templateExamples = new BsonDocument[templates.size()];

		int totalWeight = getTotalWeight();
		int docCount = 0;
		int templateIndex = -1;
		int docLimit = -1;
		Template template = null;
		PlaceholderParser parser = null;

		// initialise doc list
		for (int i = 0; i < poolSize; i++) {

			// switch to the next template in the list
			if (docCount > docLimit && templateIndex < templates.size()) {
				templateIndex++;
				template = templates.get(templateIndex);
				docLimit = poolSize * template.getWeighting() / totalWeight;
				parser = template.createPlaceholderParser();
				placeholders.addAll(parser.getPlaceholders());
				docCount = 0;
			}

			BsonDocument doc = template.getTemplate().clone();
			documents.add(doc);
			templateExamples[templateIndex] = doc;
			placeholderLocations.addAll(parser.parseDocument(documents.get(i)));
			docCount++;
		}
	}

	public List<BsonDocument> getDocuments() {
		return Collections.unmodifiableList(documents);
	}

	private int getTotalWeight() {
		int total = 0;
		for (Template template : templates) {
			total += template.getWeighting();
		}
		return total;
	}

	/**
	 * Regenerates all the placeholder values of all records.
	 *
	 * Called prior to executing an operation.
	 */
	public void regenerateValues() {
		for (Placeholder placeholder : placeholders) {
			placeholder.nextBatch();
		}
		for (PlaceholderLocation locator : placeholderLocations) {
			locator.apply();
		}
	}

}