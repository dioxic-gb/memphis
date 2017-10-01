package com.mongodb.memphis.config.operations;

import java.util.List;

import org.bson.BsonDocument;

import com.mongodb.client.MongoCollection;
import com.mongodb.memphis.DocumentPool;
import com.mongodb.memphis.annotations.Name;
import com.mongodb.memphis.config.Operation;

@Name("find")
public class Find extends Operation {

	private int iterations;

	@Override
	public final int getIterations() {
		return iterations;
	}

	@Override
	protected void execute(MongoCollection<BsonDocument> collection, List<BsonDocument> documents) {
		// TODO Auto-generated method stub

	}

	@Override
	protected DocumentPool createDocumentPool() {
		// TODO Auto-generated method stub
		return null;
	}

}
