package com.mongodb.memphis.mutators;

import org.bson.BsonValue;

import com.mongodb.memphis.engine.EngineDocument;

public class EpochMutator extends Mutator {

	@Override
	public BsonValue getValue(EngineDocument engineDocument) {
//		for (String epochIndex : config.getEpochIndex()) {
//			BsonValue queryValue = document.get(epochIndex);
//			if (queryValue != null && queryValue.isString()) {
//				String value = queryValue.asString().getValue();
//				long epoch = 0l;
//				if (value.length() > 10) {
//					epoch = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(value)).getEpochSecond();
//				} else {
//					epoch = LocalDate.from(DateTimeFormatter.ISO_DATE.parse(value)).toEpochDay();
//				}
//				document.put(epochIndex + config.getEpochSuffix(), new BsonInt32((int) epoch));
//			}
//		}
		return null;
	}

}
