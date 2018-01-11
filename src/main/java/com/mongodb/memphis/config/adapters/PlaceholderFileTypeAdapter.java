package com.mongodb.memphis.config.adapters;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mongodb.memphis.placeholder.PlaceholderFactory;
import com.mongodb.memphis.placeholder.PlaceholderFile;

public class PlaceholderFileTypeAdapter extends TypeAdapter<PlaceholderFile> {

	@Override
	public void write(JsonWriter out, PlaceholderFile value) throws IOException {

	}

	@Override
	public PlaceholderFile read(JsonReader in) throws IOException {
		return PlaceholderFactory.getInstance().loadFromFile(in.nextString());
	}

}
