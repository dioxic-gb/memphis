package com.mongodb.memphis.config.adapters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.BsonValue;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class BsonValueTypeAdapter extends TypeAdapter<BsonValue> {

	@Override
	public void write(JsonWriter out, BsonValue value) throws IOException {
		if (value != null) {
			out.value(value.toString());
		}
		else {
			out.nullValue();
		}
	}

	@Override
	public BsonValue read(JsonReader in) throws IOException {

		switch (in.peek()) {
		case STRING:
			return new BsonString(in.nextString());
		case NUMBER:
			return new BsonInt32(in.nextInt());
		case BOOLEAN:
			return new BsonBoolean(in.nextBoolean());
		case BEGIN_ARRAY:
			in.beginArray();
			List<BsonValue> array = new ArrayList<>();
			while (in.hasNext()) {
				BsonValue val = read(in);
				if (val != null) {
					array.add(val);
				}
				else {
					in.endArray();
					return new BsonArray(array);
				}
			}
		case END_ARRAY:
			return null;
		case BEGIN_OBJECT:
			in.beginObject();
			BsonDocument doc = new BsonDocument();
			while (in.hasNext()) {
				String k = in.nextName();
				BsonValue v = read(in);
				if (v != null) {
					doc.put(k, v);
				}
				else {
					in.endObject();
					return doc;
				}
			}
		case END_OBJECT: {
			return null;
		}
		default:
			throw new IllegalArgumentException(in.peek() + " is not parseable");
		}

	}

}
