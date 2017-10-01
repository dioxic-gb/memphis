package com.mongodb.memphis.queryrun.config.adapters;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mongodb.ReadConcern;
import com.mongodb.ReadConcernLevel;

public class ReadConcernTypeAdapter extends TypeAdapter<ReadConcern> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void write(JsonWriter out, ReadConcern value) throws IOException {
		if (value != null) {
			out.value(value.asDocument().get("level").asString().getValue());
		}
		else {
			out.nullValue();
		}
	}

	@Override
	public ReadConcern read(JsonReader in) throws IOException {
		return new ReadConcern(ReadConcernLevel.fromString(in.nextString()));
	}

}
