package com.mongodb.memphis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.BsonDocument;
import org.bson.RawBsonDocument;
import org.bson.codecs.BsonDocumentCodec;

import com.mongodb.annotations.NotThreadSafe;
import com.mongodb.memphis.config.PlaceholderParser;
import com.mongodb.memphis.config.PlaceholderParser.PlaceHolderLocation;
import com.mongodb.memphis.config.Template;
import com.mongodb.memphis.placeholder.Placeholder;

/**
 * Holds a pool of documents that can be reused in every batch run.
 *
 * @author Mark Baker-Munton
 */
@NotThreadSafe
public class DocumentPool {

	private int poolSize;
	private List<Template> templates;
	private List<BsonDocument> documents;
	private BsonDocument[] templateExamples;
	private List<PlaceHolderLocation> placeHolderLocations;
	private Collection<Placeholder> placeholders;

	private DocumentPool() {
	}

	private void initialise() {
		documents = new ArrayList<>(poolSize);
		placeHolderLocations = new ArrayList<>(poolSize);
		placeholders = new ArrayList<>();
		templateExamples = new BsonDocument[templates.size()];

		int totalWeight = getTotalWeight();
		int docCount = 0;
		int templateIndex = 0;
		int docLimit = -1;
		Template template = null;
		PlaceholderParser parser = null;

		// initialise doc list
		for (int i = 0; i < poolSize; i++) {

			// switch to the next template in the list
			if (docCount > docLimit && templateIndex < templates.size()) {
				template = templates.get(templateIndex);
				docLimit = poolSize * template.getWeighting() / totalWeight;
				parser = template.createPlaceholderParser();
				placeholders.addAll(parser.getPlaceholders());
				docCount = 0;
				templateIndex++;
			}

			BsonDocument doc = template.getTemplate().clone();
			documents.add(doc);
			templateExamples[templateIndex] = doc;
			placeHolderLocations.addAll(parser.parseDocument(documents.get(i)));
			docCount++;
		}
	}

	/**
	 * Calculates the average document size in the pool. Only one document per template is evaluated to avoid overhead with object creation.
	 * <p>
	 * This should be called after the placeholder values have been applied for the results to be accurate.
	 * @return average document size in bytes
	 */
	public long getAverageDocumentSize() {
		return Arrays.stream(templateExamples).collect(Collectors.averagingLong(d -> {
			return new RawBsonDocument(d, new BsonDocumentCodec()).getByteBuffer().limit();
		})).longValue();
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

		public DocumentPool build() {
			DocumentPool pool = new DocumentPool();
			pool.poolSize = poolSize;
			pool.templates = templates;
			pool.initialise();
			return pool;
		}
	}

}
