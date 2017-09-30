package com.mongodb.mepee.queryrun.data;

import java.util.Collections;
import java.util.List;

import org.bson.BsonDocument;

public class SimpleDbPopulation extends AbstractDbPopulation {
	
	@SuppressWarnings("unchecked")
	protected List<BsonDocument> initialPipeline() {
		return Collections.EMPTY_LIST;
	}

}
