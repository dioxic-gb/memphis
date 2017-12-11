package com.mongodb.memphis.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.BsonDocument;
import org.bson.RawBsonDocument;
import org.bson.codecs.BsonDocumentCodec;

import com.mongodb.memphis.config.Template;
import com.mongodb.memphis.placeholder.Placeholder;
import com.mongodb.memphis.placeholder.PlaceholderParser;
import com.mongodb.memphis.placeholder.location.PlaceholderLocation;

public class DocumentPool {
	private int batchOffset;
	protected int batchSize;
	protected List<Template> templates;
	protected List<PoolDocument> batch;
	protected List<PoolDocument> pool;
	protected BsonDocument[] templateExamples;
	protected Map<Template, Integer> documentSizes;

	public DocumentPool(List<Template> templates, int batchSize) {
		this.templates = templates;
		this.batchSize = batchSize;
		this.batch = new ArrayList<>(batchSize);
		this.documentSizes = new HashMap<>(templates.size());
		// initialise();
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
		Map<Template, PlaceholderParser> parserMap = new HashMap<>(templates.size());

		templateExamples = new BsonDocument[templates.size()];

		for (Template t : weightedList) {
			PoolDocument poolDoc = new PoolDocument();

			PlaceholderParser parser = parserMap.get(t);
			if (parser == null) {
				parser = t.createPlaceholderParser();
				parserMap.put(t, parser);
			}

			poolDoc.placeholders = parser.getPlaceholders();
			poolDoc.document = t.getTemplate().clone();
			poolDoc.placeholderLocations = parser.parseDocument(poolDoc.document);

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
		Set<Placeholder> batchPlaceHolders = batch.stream()
				.map(PoolDocument::getPlaceholders)
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());

		// signal to placeholders this is a new batch
		batchPlaceHolders.forEach(o -> o.nextBatch(iteration));

		// regenerate placeholder values for this batch
		batch.forEach(PoolDocument::recalculatePlaceholders);
	}

	public long getBatchSize() {
		return batch.stream().mapToLong(PoolDocument::getSize).sum();
	}

	public List<BsonDocument> getBatchDocuments() {
		return Collections.unmodifiableList(batch.stream().map(PoolDocument::getDocument).collect(Collectors.toList()));
	}

	public BsonDocument getDocument() {
		return getBatchDocuments().get(0);
	}

	public class PoolDocument {
		Template template;
		BsonDocument document;
		List<PlaceholderLocation> placeholderLocations;
		Collection<Placeholder> placeholders;
		Integer size;

		Collection<Placeholder> getPlaceholders() {
			return placeholders;
		}

		BsonDocument getDocument() {
			return document;
		}

		Integer getSize() {
			return size;
		}

		void recalculatePlaceholders() {
			for (PlaceholderLocation locator : placeholderLocations) {
				locator.apply();
			}

			// calculate size if not already present
			if (size == null) {
				size = documentSizes.get(template);
				if (size == null) {
					// we'll cache this value since it will be the same for all other documents from the same template
					size = new Integer(new RawBsonDocument(document, new BsonDocumentCodec()).getByteBuffer().limit());
					documentSizes.put(template, size);
				}
			}
		}
	}

}