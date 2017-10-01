package com.mongodb.memphis.queryrun.placeholder;

import org.bson.BsonValue;

public interface Placeholder {

	BsonValue getValue();
	
	void nextBatch();
}
