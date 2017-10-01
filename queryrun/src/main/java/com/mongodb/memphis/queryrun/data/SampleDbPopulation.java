package com.mongodb.memphis.queryrun.data;

import java.util.Arrays;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.BsonInt32;

import com.mongodb.memphis.queryrun.Config;

public class SampleDbPopulation extends AbstractDbPopulation {

	@Override
	protected List<BsonDocument> initialPipeline() {
		return Arrays.asList(new BsonDocument("$sample", new BsonDocument("size", new BsonInt32(sampleSize))));
	}

}
