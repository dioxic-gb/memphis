package com.mongodb.mepee.queryrun.generator;

public interface BatchAwareGenerator extends Generator {
	
	void nextBatch();
	
}
