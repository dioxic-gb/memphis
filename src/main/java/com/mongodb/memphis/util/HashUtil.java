package com.mongodb.memphis.util;

import java.nio.ByteBuffer;

import org.apache.commons.codec.digest.DigestUtils;
import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonDateTime;
import org.bson.BsonDecimal128;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.ByteBuf;
import org.bson.RawBsonDocument;
import org.bson.codecs.BsonDocumentCodec;

public class HashUtil {

	public static byte[] md5(BsonDocument document) {
		return DigestUtils.md5(getRawBsonBytes(document));
	}

	public static byte[] md5(BsonArray array) {
		return md5(new BsonDocument("array", array));
	}

	public static byte[] md5(BsonBinary binary) {
		return DigestUtils.md5(binary.getData());
	}

	public static byte[] md5(BsonString string) {
		return DigestUtils.md5(string.getValue());
	}

	public static byte[] md5(BsonDateTime dateTime) {
		return md5(dateTime.getValue());
	}

	public static byte[] md5(BsonInt64 int64) {
		return md5(int64.getValue());
	}

	public static byte[] md5(long l) {
		return DigestUtils.md5(ByteBuffer.allocate(8).putLong(l).array());
	}

	public static byte[] md5(BsonDecimal128 decimal) {
		return md5(decimal.longValue());
	}

	public static byte[] md5(BsonValue bsonValue) {
		if (bsonValue.isDocument()) {
			return md5(bsonValue.asDocument());
		}
		if (bsonValue.isString()) {
			return md5(bsonValue.asString());
		}
		if (bsonValue.isArray()) {
			return md5(bsonValue.asArray());
		}
		if (bsonValue.isBinary()) {
			return md5(bsonValue.asBinary());
		}
		if (bsonValue.isInt64()) {
			return md5(bsonValue.asInt64());
		}
		if (bsonValue.isDateTime()) {
			return md5(bsonValue.asDateTime());
		}
		if (bsonValue.isDecimal128()) {
			return md5(bsonValue.asDecimal128());
		}

		throw new UnsupportedOperationException("BsonType " + bsonValue.getBsonType() + " not supported");
	}

	private static byte[] getRawBsonBytes(BsonDocument doc) {
		ByteBuf buf = new RawBsonDocument(doc, new BsonDocumentCodec()).getByteBuffer();
		byte[] bytes = new byte[buf.limit()];
		buf.get(bytes);
		return bytes;
	}
}
