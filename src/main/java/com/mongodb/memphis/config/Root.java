package com.mongodb.memphis.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.bson.conversions.Bson;
import org.reflections.Reflections;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.memphis.annotations.Name;
import com.mongodb.memphis.config.adapters.BsonTypeAdapter;
import com.mongodb.memphis.config.adapters.ConfigPostProcessor;
import com.mongodb.memphis.config.adapters.IndexModelAdapterFactory;
import com.mongodb.memphis.config.adapters.PlaceholderFileTypeAdapter;
import com.mongodb.memphis.config.adapters.ReadConcernTypeAdapter;
import com.mongodb.memphis.config.adapters.ReadPreferenceTypeAdapter;
import com.mongodb.memphis.config.adapters.WriteConcernTypeAdapter;
import com.mongodb.memphis.operation.Operation;
import com.mongodb.memphis.operation.SampleData;
import com.mongodb.memphis.placeholder.PlaceholderFile;
import com.mongodb.memphis.util.FileUtil;
import com.mongodb.memphis.util.gson.typeadapters.RuntimeTypeAdapterFactory;

public class Root extends Config {

	private static final Collection DEFAULT_COLLECTION = new Collection("memphis");
	private static final String DEFAULT_DATABASE = "test";
	private static Gson gson;

	static {
		Reflections reflections = new Reflections("com.mongodb.memphis");

		RuntimeTypeAdapterFactory<Operation> operationAdapterFactory = RuntimeTypeAdapterFactory
				.of(Operation.class, "type");

		for (Class<? extends Operation> clazz : reflections.getSubTypesOf(Operation.class)) {
			Name annotation = clazz.getAnnotation(Name.class);
			if (annotation != null) {
				operationAdapterFactory.registerSubtype(clazz, annotation.value());
			}
		}

		gson = new GsonBuilder()
				.setPrettyPrinting()
				//.excludeFieldsWithoutExposeAnnotation()
				.registerTypeAdapterFactory(operationAdapterFactory)
				.registerTypeAdapterFactory(new IndexModelAdapterFactory())
				.registerTypeAdapterFactory(new ConfigPostProcessor())
				.registerTypeAdapter(PlaceholderFile.class, new PlaceholderFileTypeAdapter())
				.registerTypeAdapter(WriteConcern.class, new WriteConcernTypeAdapter())
				.registerTypeAdapter(ReadConcern.class, new ReadConcernTypeAdapter())
				.registerTypeAdapter(ReadPreference.class, new ReadPreferenceTypeAdapter())
				.registerTypeAdapter(Bson.class, new BsonTypeAdapter())
				.create();
	}

	private String mongoUri = "localhost:27017";
	private List<Test> tests;
	private List<SampleData> samplers;

	private transient MongoClient client;

	@Override
	public MongoClient getMongoClient() {
		return client;
	}

	public List<Test> getTests() {
		return tests;
	}

	public List<SampleData> getSamplers() {
		return samplers;
	}

	@Override
	public void initialise() {
		// set timeout to 1hr
		MongoClientOptions.Builder builder = MongoClientOptions.builder()
				.socketTimeout(60 * 60 * 1000)
				.applicationName("Memphis")
				.maxConnectionIdleTime(0);

		client = new MongoClient(new MongoClientURI("mongodb://" + mongoUri, builder));
		collection = DEFAULT_COLLECTION;
		database = DEFAULT_DATABASE;

		super.initialiseHierarchy(null, 0);
	}

	@Override
	protected void executeInternal() {
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
	protected Root getRoot() {
		return this;
	}

	@Override
	public List<Test> getChildren() {
		return tests;
	}

	public static Root loadFromJson(String configJson) {
		return gson.fromJson(configJson, Root.class);
	}

	public static Root loadFromFile(String filename) throws IOException {
		Path configFile = Paths.get(filename);
		FileUtil.addDirectory(configFile.getParent().toAbsolutePath().toString());
		String configJson = new String(Files.readAllBytes(configFile), StandardCharsets.UTF_8);
		Root root = loadFromJson(configJson);
		return root;
	}

	@Override
	public String toString() {
		return gson.toJson(this);
	}

}
