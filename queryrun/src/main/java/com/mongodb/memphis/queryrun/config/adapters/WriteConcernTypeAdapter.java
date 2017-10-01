package com.mongodb.memphis.queryrun.config.adapters;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mongodb.WriteConcern;

public class WriteConcernTypeAdapter extends TypeAdapter<WriteConcern> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void write(JsonWriter out, WriteConcern value) throws IOException {
		if (value != null) {
			out.beginObject();
			if (value.getWObject() instanceof String) {
				out.name("w").value(value.getWString());
			}
			if (value.getWObject() instanceof Integer) {
				out.name("w").value(value.getW());
			}
			out.name("wTimeout").value(value.getWTimeout(TimeUnit.MILLISECONDS));
			out.name("journal").value(value.getJournal());
			out.endObject();
		}
		else {
			out.nullValue();
		}
	}

	@Override
	public WriteConcern read(JsonReader in) throws IOException {
		WriteConcern wc = WriteConcern.UNACKNOWLEDGED;

		switch (in.peek().name()) {
		case "BEGIN_OBJECT":
			in.beginObject();
			while (in.hasNext()) {
				String wOption = in.nextName();
				switch (wOption) {
				case "w":
					String wType = in.peek().name();
					switch (wType) {
					case "STRING":
						wc = getWriteConcern(in.nextString());
						break;
					case "NUMBER":
						wc = wc.withW(in.nextInt());
						break;
					default:
						throw new IllegalStateException("could not parse writeconcern w for type [" + wType + "]");
					}
					break;
				case "wTimeout":
					wc = wc.withWTimeout(in.nextLong(), TimeUnit.MILLISECONDS);
					break;
				case "journal":
					wc = wc.withJournal(in.nextBoolean());
					break;
				default:
					throw new IllegalStateException("could not parse writeconcern option [" + wOption + "]");
				}
			}
			in.endObject();
			break;
		case "STRING":
			wc = getWriteConcern(in.nextString());
			break;
		case "NUMBER":
			wc = wc.withW(in.nextInt());
			break;
		default:
			throw new IllegalStateException("could not parse writeconcern of type " + in.peek().name());
		}

		return wc;
	}

	private WriteConcern getWriteConcern(String wString) {
		WriteConcern wc = WriteConcern.valueOf(wString);

		if (wc == null) {
			wc = new WriteConcern(wString);
			if (wString.toString().matches("\\d+")) {
				logger.warn("unless \"{}\" is an acknowledgement tag set, you probably meant to set the writeconern as a number not a string", wString);
			}
		}

		return wc;
	}

}
