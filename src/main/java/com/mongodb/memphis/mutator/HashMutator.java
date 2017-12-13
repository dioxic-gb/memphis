package com.mongodb.memphis.mutator;

import java.nio.ByteBuffer;
import java.util.Optional;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;
import com.mongodb.memphis.engine.EngineDocument;

@Name("hash")
public class HashMutator extends Mutator {

	Mode mode = Mode.INT32;

	@Override
	public BsonValue getValue(EngineDocument engineDocument) {
		Optional<String> opt = Optional.ofNullable(engineDocument)
				.map(EngineDocument::getDocument)
				.map(o -> o.get(input))
				.filter(BsonValue::isString)
				.map(BsonValue::asString)
				.map(BsonString::getValue);

		if (opt.isPresent()) {
			byte[] md5 = DigestUtils.md5(opt.get());

			switch (mode) {
			case HEX:
				return new BsonString(Hex.encodeHexString(md5));
			case INT64:
				return new BsonInt64(ByteBuffer.wrap(md5).getLong());
			default:
				return new BsonInt32(ByteBuffer.wrap(md5).getInt());
			}
		}
		else {
			logger.warn("unable to hash field {}", input);
			return new BsonNull();
		}
	}

	enum Mode {
		HEX, INT32, INT64
	}

}
