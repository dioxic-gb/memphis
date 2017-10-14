package com.mongodb.memphis.operations;

import java.util.List;

import com.mongodb.client.model.IndexModel;
import com.mongodb.memphis.annotations.Name;

@Name("createIndex")
public class CreateIndex extends Operation {

	private List<IndexModel> indexes;

	@Override
	protected void executeInternal() {
		getMongoCollection().createIndexes(indexes);
	}

}
