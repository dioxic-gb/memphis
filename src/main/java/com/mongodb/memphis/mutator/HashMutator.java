package com.mongodb.memphis.mutator;

import java.nio.ByteBuffer;
import java.util.Optional;

import org.apache.commons.codec.binary.Hex;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;
import com.mongodb.memphis.engine.EngineDocument;
import com.mongodb.memphis.util.HashUtil;

@Name("hash")
public class HashMutator extends Mutator {

	Mode mode = Mode.INT32;

	@Override
	public BsonValue getValue(EngineDocument engineDocument, String[] attributes) {
		String input = attributes.length > 1 ? attributes[1] : null;

		Optional<BsonValue> opt = Optional.ofNullable(engineDocument)
				.map(EngineDocument::getDocument)
				.map(o -> o.get(input));

		if (opt.isPresent()) {
			byte[] md5 = HashUtil.md5(opt.get());

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
