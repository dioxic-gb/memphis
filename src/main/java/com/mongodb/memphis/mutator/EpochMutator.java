package com.mongodb.memphis.mutator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.Optional;

import org.bson.BsonDateTime;
import org.bson.BsonInt32;
import org.bson.BsonNull;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;
import com.mongodb.memphis.engine.EngineDocument;

@Name("epoch")
public class EpochMutator extends Mutator {

	ChronoField chronoField;

	@Override
	public BsonValue getValue(EngineDocument engineDocument) {
		Optional<Long> opt = Optional.ofNullable(engineDocument)
			.map(EngineDocument::getDocument)
			.map(o -> o.get(input))
			.filter(BsonValue::isDateTime)
			.map(BsonValue::asDateTime)
			.map(BsonDateTime::getValue);

		if (opt.isPresent()) {
			if (chronoField != null) {
				LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(opt.get()), ZoneOffset.UTC);
				return new BsonInt32(dt.get(chronoField));
			}
			else {
				// return the MSB
				return new BsonInt32((int)(opt.get() & Integer.MAX_VALUE));
			}
		}
		else {
			logger.warn("unable to get epoch value for field {}", input);
			return new BsonNull();
		}
	}

}
