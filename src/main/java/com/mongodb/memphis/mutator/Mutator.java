package com.mongodb.memphis.mutator;

import java.util.Objects;
import java.util.Optional;

import org.bson.BsonNull;
import org.bson.BsonValue;

import com.mongodb.memphis.engine.DocumentPool.Batch;
import com.mongodb.memphis.engine.EngineDocument;
import com.mongodb.memphis.placeholder.Placeholder;

public abstract class Mutator extends Placeholder {

	@Override
	public void initialise() {
		priority = 0;
	}

	@Override
	public final BsonValue getScopedValue(EngineDocument engineDocument, Batch batch, String[] attributes) {
		Objects.requireNonNull(engineDocument, "engine document cannot be null");
		Objects.requireNonNull(batch, "batch cannot be null");
		Objects.requireNonNull(attributes, "attributes cannot be null");

		// source key
		String key = attributes[1];

		BsonValue value = Optional.ofNullable(engineDocument.getPlaceholder(key))
				.map(o -> o.getScopedValue(engineDocument, batch, attributes))
				.orElse(engineDocument.getFieldValue(key));

		if (value != null) {
			return mutate(value, attributes);
		}
		else {
			logger.warn("unable to mutate field {}", key);
			return new BsonNull();
		}
	}

	protected abstract BsonValue mutate(BsonValue value, String[] attributes);

	@Override
	public BsonValue getValue() {
		throw new UnsupportedOperationException("this method is not supported by mutator placeholders");
	}

}
