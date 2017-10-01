package com.mongodb.memphis.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPopulation implements Population {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private List<BsonDocument> population;
	protected final Set<String> fieldList;
	private BsonDocument currentDocument;
	private int counter = -1;

	public AbstractPopulation() {
		fieldList = new HashSet<>();
	}
	
	protected abstract List<BsonDocument> loadInternal();
	
	public void loadData() {
		if (population == null && !fieldList.isEmpty()) {
			logger.debug("Loading population data");

			population = loadInternal();

			if (population.isEmpty()) {
				throw new IllegalStateException("Failed to load any sample records");
			}

			if (logger.isTraceEnabled()) {
				population.forEach(x -> {
					logger.trace("population record: {}", x.toJson());
				});
			}

			logger.info("Loaded {} population records", population.size());

			next();
		}
	}

	@Override
	public BsonValue getValue(String key) {
		if (population == null) {
			throw new IllegalStateException("No data loaded!");
		}
		return currentDocument.get(key);
	}

	@Override
	public void next() {
		if (population != null) {
			counter = (counter + 1) % population.size();
			currentDocument = population.get(counter);
		} else {
			throw new IllegalStateException("No data loaded!");
		}
	}

	@Override
	public void addField(String fieldName) {
		fieldList.add(fieldName);
	}

	@Override
	public void setFields(Collection<String> fieldNames) {
		fieldList.addAll(fieldNames);
	}

}