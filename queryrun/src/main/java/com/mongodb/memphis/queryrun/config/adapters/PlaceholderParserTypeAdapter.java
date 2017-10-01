package com.mongodb.memphis.queryrun.config.adapters;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mongodb.memphis.queryrun.config.PlaceholderParser;

public class PlaceholderParserTypeAdapter extends TypeAdapter<PlaceholderParser> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void write(JsonWriter out, PlaceholderParser value) throws IOException {
		if (value != null) {
			out.value(value.getPlaceholderFile());
		}
		else {
			out.nullValue();
		}
	}

	@Override
	public PlaceholderParser read(JsonReader in) throws IOException {
		return new PlaceholderParser(in.nextString());
	}

}
