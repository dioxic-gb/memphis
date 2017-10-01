package com.mongodb.memphis.queryrun.query;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import com.mongodb.memphis.queryrun.Config;
import com.mongodb.memphis.queryrun.config.PlaceholderParser;
import com.mongodb.memphis.queryrun.config.PlaceholderParser.PlaceHolderLocation;
import com.mongodb.memphis.queryrun.mutators.Mutator;

public abstract class AbstractQuery implements Query {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private final String rawQuery;
	protected final Config config;
	protected final List<Mutator> mutators;
	protected final List<BsonDocument> bsonList;
	private final List<PlaceHolderLocation> locations;

	protected AbstractQuery(String query, Config config, PlaceholderParser parser, List<Mutator> mutators) {
		this.rawQuery = query;
		this.config = config;
		this.mutators = mutators;

		bsonList = BsonArray.parse(query)
				.getValues()
				.stream()
				.map(d -> d.asDocument())
				.collect(Collectors.toList());

		// initialise placeholder parser
		locations = parser != null ? parser.parseDocument(getMatchDocument()) : null;
	}

	/* (non-Javadoc)
	 * @see com.mongodb.johnlpage.scalingtest.queryrun.query.Query#getQueryString()
	 */
	@Override
	public String getRawQueryString() {
		return rawQuery;
	}

	@Override
	public Collection<String> getMatchFieldKeys() {
		return getMatchDocument().keySet();
	}

	@Override
	public void putMatchField(String key, BsonValue value) {
		getMatchDocument().put(key, value);
	}

	@Override
	public void removeMatchField(String key) {
		getMatchDocument().remove(key);
	}

	@Override
	public void setMatchFieldValues(BsonDocument matchFields) {
		for (String matchField : matchFields.keySet()) {
			getMatchDocument().put(matchField, matchFields.get(matchField));
		}
	}

	@Override
	public BsonValue getMatchFieldValue(String key) {
		return getMatchDocument().get(key);
	}

	protected abstract <TDocument> MongoIterable<TDocument> executeOperation(MongoCollection<TDocument> collection);

	@Override
	public <TDocument> MongoIterable<TDocument> execute(MongoCollection<TDocument> collection) {
		applyPlaceholders();
		applyMutators();
		if (logger.isDebugEnabled()) {
			logger.debug("executing {}", toString());
		}
		return executeOperation(collection);
	}

	/**
	 * returns the BsonDocument used to perform the match
	 * <br>
	 * $match for an aggregate operation
	 * <br>
	 * query input for a find operation
	 */
	protected abstract BsonDocument getMatchDocument();

	protected void applyMutators() {
		if (mutators != null) {
			for (Mutator mutator : mutators) {
				logger.trace("applying {}", mutator.toString());
				mutator.mutate(getMatchDocument());
			}
		}
	}

	public void applyPlaceholders() {
		for (PlaceHolderLocation location : locations) {
			logger.trace("applying {}", location.toString());
			location.apply();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (rawQuery == null ? 0 : rawQuery.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AbstractQuery other = (AbstractQuery) obj;
		if (rawQuery == null) {
			if (other.rawQuery != null) {
				return false;
			}
		}
		else if (!rawQuery.equals(other.rawQuery)) {
			return false;
		}
		return true;
	}

}
