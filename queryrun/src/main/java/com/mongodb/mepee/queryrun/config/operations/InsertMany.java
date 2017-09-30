package com.mongodb.mepee.queryrun.config.operations;

import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.mepee.queryrun.config.Operation;

public class InsertMany extends Operation {

	private int threads;
	private int totalDocuments;
	private int batchSize;
	private InsertManyOptions options;

	public final int getThreads() {
		return threads;
	}

	public final int getTotalDocuments() {
		return totalDocuments;
	}

	public final int getBatchSize() {
		return batchSize;
	}

	public final InsertManyOptions getOptions() {
		return options;
	}

}
