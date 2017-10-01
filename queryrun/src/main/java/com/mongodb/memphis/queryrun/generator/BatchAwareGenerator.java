package com.mongodb.memphis.queryrun.generator;

public interface BatchAwareGenerator extends Generator {
	
	void nextBatch();
	
}
