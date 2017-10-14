package com.mongodb.memphis.config;

import org.bson.conversions.Bson;

import com.mongodb.client.model.IndexOptions;

public class Index {
	private Bson fields;
	private IndexOptions options;

	public Bson getFields() {
		return fields;
	}

	public IndexOptions getOptions() {
		return options;
	}

}
