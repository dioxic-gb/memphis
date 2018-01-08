package com.mongodb.memphis.mutator;

import java.nio.ByteBuffer;

import org.apache.commons.codec.binary.Hex;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;
import com.mongodb.memphis.util.HashUtil;

@Name("hash")
public class HashMutator extends Mutator {

	Mode mode = Mode.INT32;

	@Override
	protected BsonValue mutate(BsonValue value, String[] attributes) {
		byte[] md5 = HashUtil.md5(value);

		switch (mode) {
		case HEX:
			return new BsonString(Hex.encodeHexString(md5));
		case INT64:
			return new BsonInt64(ByteBuffer.wrap(md5).getLong());
		default:
			return new BsonInt32(ByteBuffer.wrap(md5).getInt());
		}
	}

	enum Mode {
		HEX, INT32, INT64
	}

}
