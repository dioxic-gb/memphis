package com.mongodb.memphis.config.adapters;

import java.io.IOException;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.conversions.Bson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class BsonTypeAdapter extends TypeAdapter<Bson> {

	@Override
	public void write(JsonWriter out, Bson value) throws IOException {
		if (value != null) {
			out.value(value.toString());
		}
		else {
			out.nullValue();
		}
	}

	@Override
	public Bson read(JsonReader in) throws IOException {
		BsonDocument value = new BsonDocument();

		in.beginObject();

		while (in.hasNext()) {
			String key = in.nextName();

			switch (in.peek()) {
			case STRING:
				value.put(key, new BsonString(in.nextString()));
				break;
			case NUMBER:
				value.put(key, new BsonInt32(in.nextInt()));
				break;
			default:
				throw new IllegalArgumentException(in.peek() + " is not parseable for Bson");
			}
		}

		in.endObject();

		return value;
	}

}
