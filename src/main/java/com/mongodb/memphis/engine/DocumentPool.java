package com.mongodb.memphis.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.BsonDocument;
import org.bson.BsonValue;

import com.mongodb.memphis.config.Template;
import com.mongodb.memphis.placeholder.Placeholder;
import com.mongodb.memphis.placeholder.Placeholder.Scope;
import com.mongodb.memphis.placeholder.location.PlaceholderLocation;

public class DocumentPool {
	private int batchOffset;
	protected int batchSize;
	protected List<Template> templates;
	protected List<EngineDocument> pool;
	protected Batch batch;
	protected Map<Template, Integer> documentSizes;

	public DocumentPool(List<Template> templates, int batchSize) {
		this.templates = templates;
		this.batchSize = batchSize;
		this.documentSizes = new HashMap<>(templates.size());
		this.batch = new Batch(batchSize);
	}

	/**
	 * Calculate the minimum viable pool size that accurately depicts the template weighting ratios and
	 * is greater than or equal to the batch size.
	 */
	private int calcPoolSize() {
		int total = templates.stream().mapToInt(Template::getWeighting).sum();
		int min = templates.stream().mapToInt(Template::getWeighting).max().getAsInt();
		int divisor = 1;

		for (int i = min; i > 1; i--) {
			boolean common = true;
			for (Template t : templates) {
				if (t.getWeighting() % i > 0) {
					common = false;
					break;
				}
			}
			if (common) {
				divisor = i;
				break;
			}
		}

		int minPoolSize = total / divisor;
		int poolSize = Math.max(batchSize, minPoolSize);

		while (poolSize % minPoolSize != 0) {
			poolSize++;
		}

		return poolSize;
	}

	protected List<Template> getWeightedList() {
		int poolSize = calcPoolSize();
		List<Template> weightedList = new ArrayList<>(poolSize);

		int totalWeight = templates.stream().mapToInt(Template::getWeighting).sum();

		for (Template t : templates) {
			int count = t.getWeighting() * poolSize / totalWeight;
			for (int i = 0; i < count; i++) {
				weightedList.add(t);
			}
		}

		Collections.shuffle(weightedList);
		return weightedList;
	}

	public void initialise() {
		List<Template> weightedList = getWeightedList();
		pool = new ArrayList<>(weightedList.size());

		for (Template t : weightedList) {
			pool.add(new EngineDocument(t));
		}
	}

	public void nextBatch(int iteration) {
		batch.clear();
		for (int i = 0; i < batchSize; i++) {
			if (batchOffset >= pool.size()) {
				batchOffset = 0;
			}
			batch.add(pool.get(batchOffset));
			batchOffset++;
		}

		batch.regenerateValues();
	}

	public long getBatchSize() {
		return batch.getDocuments().stream().mapToLong(EngineDocument::getSize).sum();
	}

	public List<BsonDocument> getBatchDocuments() {
		return Collections.unmodifiableList(batch.getDocuments().stream().map(EngineDocument::getDocument).collect(Collectors.toList()));
	}

	/**
	 * Gets the first document in the batch.
	 * <br>
	 * Typically for something like an insertOne operation where the batch size is always 1
	 * @return
	 */
	public BsonDocument getDocument() {
		return getBatchDocuments().get(0);
	}

	class Batch {
		private List<EngineDocument> docs;
		private Map<Placeholder, BsonValue> placeholderValues;
		private Set<Placeholder> placeholders;

		public Batch(int batchSize) {
			docs = new ArrayList<>(batchSize);
			placeholderValues = new HashMap<>();
			placeholders = new HashSet<>();
		}

		private void add(EngineDocument document) {
			docs.add(document);
			placeholders.addAll(document.getPlaceholders());
		}

		private List<EngineDocument> getDocuments() {
			return docs;
		}

		private void clear() {
			docs.clear();
			placeholderValues.clear();
			placeholders.clear();
		}

		private void regenerateValues() {
			for (Placeholder p : placeholders) {
				if (p.getScope() == Scope.BATCH) {
					placeholderValues.put(p, p.getValue());
				}
			}

			for (EngineDocument doc : docs) {
				doc.regenerateValues(this);
			}
		}

		public void applyCachedValue(PlaceholderLocation locator) {
			locator.apply(placeholderValues.get(locator.getPlaceholder()));
		}
	}

}