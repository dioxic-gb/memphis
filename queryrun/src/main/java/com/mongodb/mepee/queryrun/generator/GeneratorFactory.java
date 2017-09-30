package com.mongodb.mepee.queryrun.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.mepee.queryrun.Config;
import com.mongodb.mepee.queryrun.data.PopulationCache;
import com.mongodb.mepee.queryrun.placeholder.DataArrayPlaceholder;
import com.mongodb.mepee.queryrun.placeholder.DataPlaceholder;

public class GeneratorFactory {
	private final static Logger logger = LoggerFactory.getLogger(GeneratorFactory.class);
	private static Map<String, Class<? extends Generator>> registryMap;

	private final Pattern pattern = Pattern.compile("([#@][a-zA-Z0-9]+)(\\.([a-zA-Z0-9]+))*(\\((.*)\\))*");
	private final Config config;
	private final PopulationCache populationCache;

	static {
		registryMap = new HashMap<>();
		registryMap.put("@double", DoubleGenerator.class);
		registryMap.put("@integer", IntegerGenerator.class);
		registryMap.put("@objectid", ObjectIdGenerator.class);
		registryMap.put("@string", StringGenerator.class);
		registryMap.put("@stringdate", StringDateGenerator.class);
		registryMap.put("@stringdatetime", StringDateTimeGenerator.class);
		registryMap.put("@uuid", UUIDGenerator.class);
		registryMap.put("@datetime", DateTimeGenerator.class);
		registryMap.put("@long", LongGenerator.class);
		registryMap.put("#data", DataPlaceholder.class);
		registryMap.put("#arraydata", DataArrayPlaceholder.class);
	}

	public static void registerGenerator(String key, Class<? extends Generator> generatorClass) {
		registryMap.put(key, generatorClass);
	}
	
	public GeneratorFactory(Config config, PopulationCache populationCache) {
		this.config = config;
		this.populationCache = populationCache;
	}

	public Generator createGenerator(String queryKey, String signature) {

		logger.info("creating generator[{}] for {}", signature, queryKey);
		
		Matcher matcher = pattern.matcher(signature);
		
		if (!matcher.find()) {
			throw new IllegalArgumentException("could not parse signature " + signature);
		}
		
		String key = matcher.group(1);
		String popName = matcher.group(3);
		String argString = matcher.group(5);
		String[] args = (argString != null) ? argString.split(",") : null;

		try {
			Class<? extends Generator> generatorClass = registryMap.get(key);
			if (generatorClass == null) {
				throw new IllegalStateException("generator class for " + key + " not found");
			}

			Generator generator = registryMap.get(key).newInstance();

			generator.setConfig(config);
			if (popName != null) {
				generator.setPopulation(populationCache.get(popName));			
			}
			generator.setQueryKey(queryKey);
			
			if (args != null) {
				generator.setArguements(args);
			}
			
			generator.init();

			return generator;
		}
		catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
