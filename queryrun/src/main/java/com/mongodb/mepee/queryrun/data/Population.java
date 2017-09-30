package com.mongodb.mepee.queryrun.data;

import java.util.Collection;

import org.bson.BsonValue;

public interface Population {

	void loadData();

	BsonValue getValue(String key);

	void next();

	void addField(String fieldName);

	void setFields(Collection<String> fieldNames);

}