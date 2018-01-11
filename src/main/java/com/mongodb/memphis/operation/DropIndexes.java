package com.mongodb.memphis.operation;

import com.mongodb.memphis.annotations.Name;

@Name("dropIndexes")
public class DropIndexes extends Operation {

	@Override
	protected void executeInternal() {
		getMongoCollection().dropIndexes();
	}

}
