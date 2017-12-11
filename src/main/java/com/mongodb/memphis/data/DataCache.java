package com.mongodb.memphis.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bson.BsonValue;

public class DataCache {

	private static Map<String, Cache> dataCache = new HashMap<>();

	public static BsonValue getValue(String cacheKey, String fieldKey, Thread thread, int iteration) {
		Cache cache = dataCache.get(cacheKey);

		if (cache == null) {
			throw new IllegalArgumentException("the data cache [" + cacheKey + "] does not exist or is empty");
		}

		return cache.getValue(fieldKey, thread, iteration);
	}

	public static void put(String cacheKey, Map<String, BsonValue> data) {
		getCache(cacheKey).put(data);
	}

	private static Cache getCache(String cacheKey) {
		Cache cache = dataCache.get(cacheKey);
		if (cache == null) {
			cache = new Cache();
			dataCache.put(cacheKey, cache);
		}
		return cache;
	}

	static class Cache {
		ConcurrentMap<Thread, Map<Integer, Map<String, BsonValue>>> currentValueMap = new ConcurrentHashMap<>();
		List<Map<String, BsonValue>> cache = new ArrayList<>();
		Random random = new Random();

		BsonValue getValue(String fieldKey, Thread thread, int iteration) {
			Map<Integer, Map<String, BsonValue>> threadMap = currentValueMap.get(thread);

			if (threadMap == null) {
				threadMap = new ConcurrentHashMap<>();
				currentValueMap.put(thread, threadMap);
			}

			Map<String, BsonValue> currentValue = threadMap.get(iteration);

			if (currentValue == null) {
				currentValue = cache.get(random.nextInt(cache.size()));
				threadMap.put(iteration, currentValue);
			}

			return currentValue.get(fieldKey);
		}

		void put(Map<String, BsonValue> data) {
			cache.add(data);
		}
	}

}
