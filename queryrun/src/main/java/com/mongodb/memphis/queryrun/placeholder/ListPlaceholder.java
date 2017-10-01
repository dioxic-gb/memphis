package com.mongodb.memphis.queryrun.placeholder;

import java.util.Random;

import org.bson.BsonArray;
import org.bson.BsonValue;

import com.mongodb.memphis.queryrun.generator.AbstractGenerator;
import com.mongodb.memphis.queryrun.generator.Generator;

public class ListPlaceholder extends AbstractGenerator implements Placeholder {

	private final Random random = new Random();

	private final BsonArray list;

	public ListPlaceholder(BsonArray list) {
		this.list = list;
	}

	@Override
	public BsonValue getValue() {
		return list.get(random.nextInt(list.size()));
	}

	@Override
	public String toString() {
		return "ListPlaceholder [list=" + list + "]";
	}
	
}
