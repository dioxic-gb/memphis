package com.mongodb.memphis.operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;
import com.mongodb.memphis.data.DataCache;

@Name("sampleData")
public class SampleData extends Operation {
	private static final char DOT_REPLACE = '_';
	private List<String> fields;
	private int size = 100;
	private String cacheKey;

	@Override
	protected void executeInternal() {
		List<BsonDocument> pipeline = new ArrayList<>();
		pipeline.add(new BsonDocument("$sample", new BsonDocument("size", new BsonInt32(size))));
		pipeline.add(new BsonDocument("$project", calculateProjection()));

		if (logger.isDebugEnabled()) {
			logger.debug("Data sample pipeline: {}", pipelineToJson(pipeline));
		}

		for (BsonDocument result : getMongoCollection().aggregate(pipeline)) {
			Map<String, BsonValue> data = new HashMap<>();
			for (String field : fields) {
				data.put(field, result.get(field.replace('.', DOT_REPLACE)));
			}
			DataCache.put(cacheKey, data);
		}
	}

	private BsonDocument calculateProjection() {
		BsonDocument doc = new BsonDocument();

		fields.stream().forEach(x -> {
			doc.put(x.replace('.', DOT_REPLACE), new BsonString("$" + x));
		});

		return doc;
	}

	private String pipelineToJson(List<BsonDocument> pipeline) {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (BsonDocument bson : pipeline) {
			sb.append(bson.toJson()).append(',');
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(']');

		return sb.toString();
	}

}
