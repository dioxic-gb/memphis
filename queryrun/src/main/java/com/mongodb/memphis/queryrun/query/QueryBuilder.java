package com.mongodb.memphis.queryrun.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.memphis.queryrun.Config;
import com.mongodb.memphis.queryrun.config.PlaceholderParser;
import com.mongodb.memphis.queryrun.mutators.Mutator;

public class QueryBuilder {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Pattern findPattern = Pattern.compile("\\w*find\\((\\{.+\\})\\)", Pattern.DOTALL);
	private final Pattern aggPattern = Pattern.compile("\\w*aggregate\\((\\[.+\\])\\)", Pattern.DOTALL);
	private String rawQuery;
	private PlaceholderParser parser;
	private Config config;
	private List<Mutator> mutators;

	private QueryBuilder() {
	}

	public static QueryBuilder create() {
		return new QueryBuilder();
	}

	public QueryBuilder setQuery(String query) throws IOException {
		rawQuery = query;
		return this;
	}

	public QueryBuilder setMutators(List<Mutator> mutators) {
		this.mutators = mutators;
		return this;
	}

	public QueryBuilder addMutator(Mutator mutator) {
		if (mutators == null) {
			mutators = new ArrayList<>();
		}
		mutators.add(mutator);
		return this;
	}

	public QueryBuilder setPlaceholderParser(PlaceholderParser placeholderParser) {
		this.parser = placeholderParser;
		return this;
	}

	public QueryBuilder setRawQuery(String query) {
		this.rawQuery = query;
		return this;
	}

	public QueryBuilder setConfig(Config config) {
		this.config = config;
		return this;
	}

	public Query build() {
		if (config == null) {
			throw new IllegalStateException("config not set!");
		}
		if (rawQuery == null) {
			throw new IllegalStateException("raw query not set!");
		}

		StringBuilder sb = new StringBuilder();
		sb.append("building query\n   ");
		if (rawQuery.length() > 2000) {
			sb.append(rawQuery, 0, 2000);
			sb.append("...");
		}
		else {
			sb.append(rawQuery);	
		}
		
		if (mutators != null) {
			for (Mutator mutator : mutators) {
				sb.append("\n with ").append(mutator.toString());
			}
		}

		logger.info(sb.toString());

		Matcher find = findPattern.matcher(rawQuery);
		Matcher agg = aggPattern.matcher(rawQuery);

		if (find.find()) {
			return new FindQuery(find.group(1), config, parser, mutators);
		} else if (agg.find()) {
			return new AggregationQuery(agg.group(1), config, parser, mutators);
		} else {
			logger.error("Query {} not valid", rawQuery);
			throw new IllegalArgumentException("query not valid!");
		}
	}
}
