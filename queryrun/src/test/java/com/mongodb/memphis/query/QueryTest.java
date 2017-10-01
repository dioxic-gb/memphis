package com.mongodb.memphis.query;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.memphis.Config;
import com.mongodb.memphis.mutators.EpochMutator;
import com.mongodb.memphis.mutators.HashMutator;
import com.mongodb.memphis.query.AggregationQuery;
import com.mongodb.memphis.query.FindQuery;
import com.mongodb.memphis.query.Query;
import com.mongodb.memphis.query.QueryBuilder;

public class QueryTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private Config config;

	@Before
	public void setup() throws IOException, URISyntaxException {
		config = Config.load(Paths.get(getClass().getClassLoader().getResource("config.json").toURI()));
	}

	@Test
	public void parseValidQueries() {
		Query query = QueryBuilder.create()
				.setRawQuery("find({msgId: \"38198206752a6b4a3-7003-4ef0-9da5-facf0fe5704e\"}, {index:1})")
				.setConfig(config)
				.build();

		assertThat(query).as("query null check").isNotNull();
		assertThat(query).as("query class type").isOfAnyClassIn(FindQuery.class);
		assertThat(query.getRawQueryString()).as("raw find query string").isEqualTo("[{msgId: \"38198206752a6b4a3-7003-4ef0-9da5-facf0fe5704e\"}, {index:1}]");

		query = QueryBuilder.create()
				.setRawQuery("aggregate([{$match: {a: 1}}, {$project: {a: 1, _id: 0}}])")
				.setConfig(config)
				.build();

		assertThat(query).as("query null check").isNotNull();
		assertThat(query).as("query class type").isOfAnyClassIn(AggregationQuery.class);
		assertThat(query.getRawQueryString()).as("raw agg query string").isEqualTo("[{$match: {a: 1}}, {$project: {a: 1, _id: 0}}]");
	}

	@Ignore
	@Test
	public void placeholderTest() {
		Query query = QueryBuilder.create()
				.setRawQuery("find({msgId: \"${PLAICE}\"}, {index:1})")
				.setConfig(config)
				// .addPlaceholder("PLAICE", "123")
				.build();

		assertThat(query).as("query null check").isNotNull();
		Assert.assertEquals("{msgId: \"123\"}, {index:1}", query.getRawQueryString());

		query = QueryBuilder.create()
				.setRawQuery("aggregate([{$match: {a: ${PLAICE}}}])")
				.setConfig(config)
				// .addPlaceholder("PLAICE", "123")
				.build();

		Assert.assertNotNull(query);
		Assert.assertEquals(AggregationQuery.class, query.getClass());
		Assert.assertEquals("[{$match: {a: 123}}]", query.getRawQueryString());
	}

	@Test
	@Ignore
	public void addHashTest() {
		Query query = QueryBuilder.create()
				.setRawQuery("find({\"index.msgId\": \"38198206752a6b4a3-7003-4ef0-9da5-facf0fe5704e\"})")
				.addMutator(new HashMutator(config))
				.setConfig(config)
				.build();

		assertThat(query).as("query null check").isNotNull();
		assertThat(((FindQuery) query).getMatchDocument()).as("msgId hash").containsKey("index.msgId" + config.getHashSuffix());

		query = QueryBuilder.create()
				.setRawQuery("aggregate([{$match: {\"index.msgId\": \"123\"}}])")
				.addMutator(new HashMutator(config))
				.setConfig(config)
				.build();

		Assert.assertNotNull(query);
		assertThat(query).as("query null check").isNotNull();
		assertThat(((AggregationQuery) query).getMatchDocument()).as("msgId hash").containsKey("index.msgId" + config.getHashSuffix());
	}

	@Test
	@Ignore
	public void addEpochTest() {
		Query query = QueryBuilder.create()
				.setRawQuery("find({\"index.tradecreationTimestamp\": \"2018-02-16T08:31:08Z\"})")
				.addMutator(new EpochMutator(config))
				.setConfig(config)
				.build();

		assertThat(query).as("query null check").isNotNull();
		assertThat(((FindQuery) query).getMatchDocument()).as("tradecreationTimestamp epoch").containsKey("index.tradecreationTimestamp" + config.getEpochSuffix());

		query = QueryBuilder.create()
				.setRawQuery("aggregate([{$match: {\"index.tradecreationTimestamp\": \"2018-02-16T08:31:08Z\"}}])")
				.addMutator(new EpochMutator(config))
				.setConfig(config)
				.build();

		assertThat(query).as("query null check").isNotNull();
		assertThat(((AggregationQuery) query).getMatchDocument()).as("tradecreationTimestamp epoch").containsKey("index.tradecreationTimestamp" + config.getEpochSuffix());
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseInvalidQueries() {
		QueryBuilder.create()
				.setRawQuery("find()")
				.setConfig(config)
				.build();
	}

}
