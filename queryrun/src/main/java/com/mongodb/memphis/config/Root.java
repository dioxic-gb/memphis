package com.mongodb.memphis.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.reflections.Reflections;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.memphis.annotations.Name;
import com.mongodb.memphis.config.adapters.ReadConcernTypeAdapter;
import com.mongodb.memphis.config.adapters.ReadPreferenceTypeAdapter;
import com.mongodb.memphis.config.adapters.WriteConcernTypeAdapter;

public class Root extends Config {

	private static Gson gson;

	static {
		Reflections reflections = new Reflections("com.mongodb.memphis");

		RuntimeTypeAdapterFactory<Operation> typeAdapterFactory = RuntimeTypeAdapterFactory
				.of(Operation.class, "type");

		for (Class<? extends Operation> clazz : reflections.getSubTypesOf(Operation.class)) {
			Name annotation = clazz.getAnnotation(Name.class);
			if (annotation != null) {
				typeAdapterFactory.registerSubtype(clazz, annotation.value());
			}
		}

		gson = new GsonBuilder()
				.setPrettyPrinting()
				//.excludeFieldsWithoutExposeAnnotation()
				.registerTypeAdapterFactory(typeAdapterFactory)
				.registerTypeAdapter(WriteConcern.class, new WriteConcernTypeAdapter())
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
				.applicationName("Memphis")
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

	public static Root loadFromJson(String configJson) {
		return gson.fromJson(configJson, Root.class);
	}

	public static Root loadFromFile(String filename) throws IOException {
		String configJson = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
		return loadFromJson(configJson);
	}

	@Override
	public String toString() {
		return gson.toJson(this);
	}

}
