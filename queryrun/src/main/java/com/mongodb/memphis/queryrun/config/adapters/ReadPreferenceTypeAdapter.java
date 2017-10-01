package com.mongodb.memphis.queryrun.config.adapters;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mongodb.ReadPreference;

public class ReadPreferenceTypeAdapter extends TypeAdapter<ReadPreference> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void write(JsonWriter out, ReadPreference value) throws IOException {
		if (value != null) {
			out.value(value.getName());
		}
		else {
			out.nullValue();
		}
	}

	@Override
	public ReadPreference read(JsonReader in) throws IOException {
		return ReadPreference.valueOf(in.nextString());
	}

}
