package com.mongodb.memphis.mutator;

import java.nio.ByteBuffer;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.bson.BsonInt32;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;
import com.mongodb.memphis.engine.EngineDocument;

@Name("hash")
public class HashMutator extends Mutator {

	@Override
	public BsonValue getValue(EngineDocument engineDocument) {
		Optional<String> opt = Optional.ofNullable(engineDocument)
				.map(EngineDocument::getDocument)
				.map(o -> o.get(input))
				.filter(BsonValue::isString)
				.map(BsonValue::asString)
				.map(BsonString::getValue);

		if (opt.isPresent()) {
			ByteBuffer buffer = ByteBuffer.wrap(DigestUtils.md5(opt.get()));
			// get least-significant bits
			buffer.position(buffer.limit() - 4);

			return new BsonInt32(buffer.getInt());
		}
		else {
			logger.warn("unable to hash field {}", input);
			return new BsonNull();
		}
	}

}
