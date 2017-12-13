package com.mongodb.memphis.mutators;

import java.nio.ByteBuffer;

import org.apache.commons.codec.digest.DigestUtils;
import org.bson.BsonInt32;
import org.bson.BsonNull;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;
import com.mongodb.memphis.engine.EngineDocument;

@Name("hashMutator")
public class HashMutator extends Mutator {

	@Override
	public BsonValue getValue(EngineDocument engineDocument) {
		BsonValue hashFieldValue = engineDocument.getDocument().get(input);
		if (hashFieldValue != null && hashFieldValue.isString()) {
			String value = hashFieldValue.asString().getValue();

			ByteBuffer buffer = ByteBuffer.wrap(DigestUtils.md5(value));
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
