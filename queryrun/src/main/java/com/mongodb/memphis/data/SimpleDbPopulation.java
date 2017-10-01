package com.mongodb.memphis.data;

import java.util.Collections;
import java.util.List;

import org.bson.BsonDocument;

public class SimpleDbPopulation extends AbstractDbPopulation {
	
	@SuppressWarnings("unchecked")
	protected List<BsonDocument> initialPipeline() {
		return Collections.EMPTY_LIST;
	}

}
