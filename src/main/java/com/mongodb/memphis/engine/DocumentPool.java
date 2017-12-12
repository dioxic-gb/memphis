package com.mongodb.memphis.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.RawBsonDocument;
import org.bson.codecs.BsonDocumentCodec;

import com.mongodb.memphis.config.Template;
import com.mongodb.memphis.placeholder.Placeholder;
import com.mongodb.memphis.placeholder.Placeholder.Mode;
import com.mongodb.memphis.placeholder.location.PlaceholderLocation;

public class DocumentPool {
	private int batchOffset;
	protected int batchSize;
	protected List<Template> templates;
	protected List<PoolDocument> pool;
	protected Batch batch;
	protected BsonDocument[] templateExamples;
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

		templateExamples = new BsonDocument[templates.size()];

		for (Template t : weightedList) {
			PoolDocument poolDoc = new PoolDocument();
			poolDoc.placeholders = t.getPlaceholders();
			poolDoc.document = t.cloneDocument();
			poolDoc.placeholderLocations = t.parseDocument(poolDoc.document);

			pool.add(poolDoc);
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

		// collect the placeholders from this batch of documents
		// Set<Placeholder> batchPlaceHolders = batch.stream()
		// .map(PoolDocument::getPlaceholders)
		// .flatMap(Collection::stream)
		// .collect(Collectors.toSet());

		// signal to placeholders this is a new batch
		// batchPlaceHolders.forEach(o -> o.nextBatch(iteration));

		// regenerate placeholder values for this batch
		batch.regenerateValues();
	}

	public long getBatchSize() {
		return batch.getDocuments().stream().mapToLong(PoolDocument::getSize).sum();
	}

	public List<BsonDocument> getBatchDocuments() {
		return Collections.unmodifiableList(batch.getDocuments().stream().map(PoolDocument::getDocument).collect(Collectors.toList()));
	}

	/**
	 * Gets the first document in the batch.
	 * <br>
	 * Typically for something an insertOne operation where the batch size is 1
	 * @return
	 */
	public BsonDocument getDocument() {
		return getBatchDocuments().get(0);
	}

	class Batch {
		private List<PoolDocument> docs;
		private Map<Placeholder, BsonValue> placeholderValues;
		private Set<Placeholder> placeholders;

		public Batch(int batchSize) {
			docs = new ArrayList<>(batchSize);
			placeholderValues = new HashMap<>();
			placeholders = new HashSet<>();
		}

		void add(PoolDocument document) {
			docs.add(document);
			placeholders.addAll(document.placeholders);
		}

		List<PoolDocument> getDocuments() {
			return docs;
		}

		void clear() {
			docs.clear();
			placeholderValues.clear();
			placeholders.clear();
		}

		void regenerateValues() {
			for (Placeholder p : placeholders) {
				if (p.getMode() == Mode.BATCH) {
					placeholderValues.put(p, p.getValue());
				}
			}

			for (PoolDocument doc : docs) {
				doc.regenerateValues(this);
			}
		}

		BsonValue getCachedValue(Placeholder placeholder) {
			return placeholderValues.get(placeholder);
		}

		void applyCachedValue(PlaceholderLocation locator) {
			locator.apply(placeholderValues.get(locator.getPlaceholder()));
		}
	}

	class PoolDocument {
		private Map<Placeholder, BsonValue> placeholderValues;
		private Integer size;
		Template template;
		BsonDocument document;
		List<PlaceholderLocation> placeholderLocations;
		Collection<Placeholder> placeholders;

		Collection<Placeholder> getPlaceholders() {
			return placeholders;
		}

		BsonDocument getDocument() {
			return document;
		}

		Integer getSize() {
			return size;
		}

		void applyCachedValue(PlaceholderLocation locator) {
			locator.apply(placeholderValues.get(locator.getPlaceholder()));
		}

		void regenerateValues(Batch batch) {
			// cache values for placeholders in DOCUMENT mode
			for (Placeholder p : placeholders) {
				if (p.getMode() == Mode.DOCUMENT) {
					// lazy load map for efficiency - mostly this won't be used
					if (placeholderValues == null) {
						placeholderValues = new HashMap<>(placeholders.size());
					}
					placeholderValues.put(p, p.getValue());
				}
			}

			// apply values to locators
			for (PlaceholderLocation locator : placeholderLocations) {
				switch (locator.getPlaceholder().getMode()) {
				case BATCH:
					batch.applyCachedValue(locator);
					break;
				case DOCUMENT:
					this.applyCachedValue(locator);
					break;
				default:
					locator.apply();
				}
			}

			// calculate size if not already present
			if (size == null) {
				size = documentSizes.get(template);
				if (size == null) {
					// we'll cache this value since it will be the same for all
					// other documents from the same template
					size = new Integer(new RawBsonDocument(document, new BsonDocumentCodec()).getByteBuffer().limit());
					documentSizes.put(template, size);
				}
			}
		}
	}

}