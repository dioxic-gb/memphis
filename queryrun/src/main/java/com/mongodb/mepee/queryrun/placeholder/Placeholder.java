package com.mongodb.mepee.queryrun.placeholder;

import org.bson.BsonValue;

public interface Placeholder {

	BsonValue getValue();
	
	void nextBatch();
}
