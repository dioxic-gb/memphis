package com.mongodb.memphis.queryrun;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bson.BsonDocument;

import com.mongodb.annotations.NotThreadSafe;
import com.mongodb.memphis.queryrun.config.Template;
import com.mongodb.memphis.queryrun.config.PlaceholderParser.PlaceHolderLocation;
import com.mongodb.memphis.queryrun.placeholder.Placeholder;

/**
 * Holds a pool of documents that can be reused in every batch run.
 *
 * @author Mark Baker-Munton
 */
@NotThreadSafe
public class ThreadBsonDocumentPool {

	private int poolSize;
	private List<Template> templates;
	private List<BsonDocument> documents;
	private List<PlaceHolderLocation> placeHolderLocations;
	private Collection<Placeholder> placeholders;

	private ThreadBsonDocumentPool() {
	}

	private void initialise() {
		documents = new ArrayList<>(poolSize);
		placeHolderLocations = new ArrayList<>(poolSize);
		placeholders = new ArrayList<>();

		int totalWeight = getTotalWeight();
		int docCount = 0;
		int templateIndex = 0;
		int docLimit = -1;
		Template template = null;

		// initialise doc list
		for (int i = 0; i < poolSize; i++) {

			// switch to the next template in the list
			if (docCount > docLimit && templateIndex < templates.size()) {
				template = templates.get(templateIndex);
				docLimit = poolSize * template.getWeighting() / totalWeight;
				placeholders.addAll(template.getPlaceHolderParser().getGenerators());
				docCount = 0;
				templateIndex++;
			}

			documents.add(template.getTemplate().clone());
			placeHolderLocations.addAll(template.getPlaceHolderParser().parseDocument(documents.get(i)));
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
		for (PlaceHolderLocation locator : placeHolderLocations) {
			locator.apply();
		}
	}

	public static ThreadBsonDocumentPoolBuilder builder() {
		return new ThreadBsonDocumentPoolBuilder();
	}

	public static class ThreadBsonDocumentPoolBuilder {
		private int poolSize;
		private List<Template> templates;

		public ThreadBsonDocumentPoolBuilder poolSize(int poolSize) {
			this.poolSize = poolSize;
			return this;
		}

		public ThreadBsonDocumentPoolBuilder templates(List<Template> templates) {
			this.templates = templates;
			return this;
		}

		public ThreadBsonDocumentPool build() {
			ThreadBsonDocumentPool pool = new ThreadBsonDocumentPool();
			pool.poolSize = poolSize;
			pool.templates = templates;
			pool.initialise();
			return pool;
		}
	}

}
