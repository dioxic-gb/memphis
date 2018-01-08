package com.mongodb.memphis.mutator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.Optional;

import org.bson.BsonDateTime;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("epoch")
public class EpochMutator extends Mutator {

	ChronoField chronoField;

	@Override
	protected BsonValue mutate(BsonValue value, String[] attributes) {
		Long epoch = Optional.of(value)
				.filter(BsonValue::isDateTime)
				.map(BsonValue::asDateTime)
				.map(BsonDateTime::getValue)
				.orElseThrow(() -> new IllegalArgumentException("BsonValue is not a datetime type"));

		if (chronoField != null) {
			LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneOffset.UTC);
			return chronoField.range().getMaximum() > Integer.MAX_VALUE ? new BsonInt64(dt.getLong(chronoField)) : new BsonInt32(dt.get(chronoField));
		}
		else {
			// return the MSB
			return new BsonInt32((int)(epoch & Integer.MAX_VALUE));
		}
	}

}
