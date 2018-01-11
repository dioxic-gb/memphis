package com.mongodb.memphis.operation;

import com.mongodb.memphis.annotations.Name;

@Name("dropCollection")
public class DropCollection extends Operation {

	@Override
	protected void executeInternal() {
		getMongoCollection().drop();
	}

}
