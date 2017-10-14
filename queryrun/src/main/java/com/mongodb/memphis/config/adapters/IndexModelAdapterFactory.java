package com.mongodb.memphis.config.adapters;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;

public class IndexModelAdapterFactory implements TypeAdapterFactory {

	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		Class<? super T> rawType = type.getRawType();
		if (!rawType.isAssignableFrom(IndexModel.class)) {
			return null;
		}
		final TypeAdapter<IndexModel> delegate = gson.getDelegateAdapter(this, TypeToken.get(IndexModel.class));

		return new TypeAdapter<T>() {

			@Override
			public void write(JsonWriter out, T value) throws IOException {
				delegate.write(out, (IndexModel)value);
			}

			@SuppressWarnings("unchecked")
			@Override
			public T read(JsonReader in) throws IOException {
				IndexModel value = delegate.read(in);
				if ( value.getOptions() == null) {
					value = new IndexModel(value.getKeys(), new IndexOptions());
				}
				return (T)value;
			}
		};
	}
}