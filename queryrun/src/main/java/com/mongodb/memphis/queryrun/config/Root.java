package com.mongodb.memphis.queryrun.config;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.memphis.queryrun.config.adapters.PlaceholderParserTypeAdapter;
import com.mongodb.memphis.queryrun.config.adapters.ReadConcernTypeAdapter;
import com.mongodb.memphis.queryrun.config.adapters.ReadPreferenceTypeAdapter;
import com.mongodb.memphis.queryrun.config.adapters.WriteConcernTypeAdapter;
import com.mongodb.memphis.queryrun.config.operations.Find;
import com.mongodb.memphis.queryrun.config.operations.InsertMany;

public class Root extends Config {

	private static Gson gson;

	static {
		RuntimeTypeAdapterFactory<Operation> runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
				.of(Operation.class, "type")
				.registerSubtype(InsertMany.class, "insertMany")
				.registerSubtype(Find.class, "find");

		gson = new GsonBuilder()
				.setPrettyPrinting()
				//.excludeFieldsWithoutExposeAnnotation()
				.registerTypeAdapterFactory(runtimeTypeAdapterFactory)
				.registerTypeAdapter(WriteConcern.class, new WriteConcernTypeAdapter())
				.registerTypeAdapter(PlaceholderParser.class, new PlaceholderParserTypeAdapter())
				.registerTypeAdapter(ReadConcern.class, new ReadConcernTypeAdapter())
				.registerTypeAdapter(ReadPreference.class, new ReadPreferenceTypeAdapter())
				.create();
	}

	private String mongoUri;
	private List<Test> tests;

	private transient MongoClient client;

	@Override
	public MongoClient getMongoClient() {
		return client;
	}

	public List<Test> getTests() {
		return tests;
	}

	@Override
	public void initialise() {
		// set timeout to 1hr
		MongoClientOptions.Builder builder = MongoClientOptions.builder()
				.socketTimeout(60 * 60 * 1000)
				.applicationName("MEPEE")
				.maxConnectionIdleTime(0);

		client = new MongoClient(new MongoClientURI("mongodb://" + mongoUri, builder));

		super.initialiseHierarchy(null);
	}

	@Override
	public void execute() {
		try {
			for (Test test : tests) {
				test.execute();
			}
		}
		finally {
			if (client != null) {
				client.close();
			}
		}
	}

	@Override
	public List<Test> getChildren() {
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
