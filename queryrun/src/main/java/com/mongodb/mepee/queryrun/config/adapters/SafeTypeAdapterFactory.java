package com.mongodb.mepee.queryrun.config.adapters;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class SafeTypeAdapterFactory implements TypeAdapterFactory {

	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {

		final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);

		return new TypeAdapter<T>() {

			@Override
			public void write(JsonWriter out, T value) throws IOException {
				try {
					delegate.write(out, value);
				}
				catch (IOException e) {
					delegate.write(out, null);
				}
			}

			@Override
			public T read(JsonReader in) throws IOException {
				try {
					return delegate.read(in);
				}
				catch (IOException e) {
					in.skipValue();
					return null;
				}
				catch (IllegalStateException e) {
					in.skipValue();
					return null;
				}
				catch (JsonSyntaxException e) {
					in.skipValue();
					return null;
				}

			}
		};
	}
}