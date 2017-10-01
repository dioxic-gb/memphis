package com.mongodb.memphis.config.adapters;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void write(JsonWriter out, LocalDateTime value) throws IOException {
		if (value != null) {
			out.value(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
		}
		else {
			out.nullValue();
		}
	}

	@Override
	public LocalDateTime read(JsonReader in) throws IOException {
		return LocalDateTime.parse(in.nextString());
	}

}
