package com.mongodb.memphis.engine;

import java.util.List;

import org.bson.BsonDocument;

import com.mongodb.memphis.config.Template;

public class QueryDocumentPool extends AbstractDocumentPool  {

	int currentQueryIndex = -1;

	public QueryDocumentPool(List<Template> templates) {
		super(templates, templates.size());
	}

	public BsonDocument getNextQuery() {
		currentQueryIndex++;

		if (currentQueryIndex == poolSize) {
			currentQueryIndex = 0;
		}

		return documents.get(currentQueryIndex);
	}

}
