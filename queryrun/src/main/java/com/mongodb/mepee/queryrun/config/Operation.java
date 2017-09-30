package com.mongodb.mepee.queryrun.config;

import java.util.List;

public abstract class Operation {

	private String database;
	private Collection collection;
	private List<Template> templates;

	public final String getDatabase() {
		return database;
	}

	public final Collection getCollection() {
		return collection;
	}

	public final List<Template> getTemplates() {
		return templates;
	}

}
