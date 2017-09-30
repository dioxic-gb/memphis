package com.mongodb.mepee.queryrun.config;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.mepee.queryrun.config.adapters.ReadConcernTypeAdapter;
import com.mongodb.mepee.queryrun.config.adapters.ReadPreferenceTypeAdapter;
import com.mongodb.mepee.queryrun.config.adapters.WriteConcernTypeAdapter;
import com.mongodb.mepee.queryrun.config.operations.Find;
import com.mongodb.mepee.queryrun.config.operations.InsertMany;

public class Root {

	private static Gson gson;

	static {
		RuntimeTypeAdapterFactory<Operation> runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
				.of(Operation.class, "type")
				.registerSubtype(InsertMany.class, "insertMany")
				.registerSubtype(Find.class, "find");

		gson = new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapterFactory(runtimeTypeAdapterFactory)
				.registerTypeAdapter(WriteConcern.class, new WriteConcernTypeAdapter())
				.registerTypeAdapter(ReadConcern.class, new ReadConcernTypeAdapter())
				.registerTypeAdapter(ReadPreference.class, new ReadPreferenceTypeAdapter())
				.create();
	}

	private String mongoUri;
	private String database;
	private String collection;
	private List<Test> tests;

	public String getMongoUri() {
		return mongoUri;
	}

	public String getDatabase() {
		return database;
	}

	public String getCollection() {
		return collection;
	}

	public List<Test> getTests() {
		return tests;
	}

	public static Root load(String configJson) {
		return gson.fromJson(configJson, Root.class);
	}

	@Override
	public String toString() {
		return gson.toJson(this);
	}

}
