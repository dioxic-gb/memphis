package com.mongodb.memphis.queryrun.config.operations;

import java.util.List;

import org.bson.BsonDocument;

import com.mongodb.client.MongoCollection;
import com.mongodb.memphis.queryrun.ThreadBsonDocumentPool;
import com.mongodb.memphis.queryrun.config.Operation;

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
	protected ThreadBsonDocumentPool getDocumentPool() {
		// TODO Auto-generated method stub
		return null;
	}

}
