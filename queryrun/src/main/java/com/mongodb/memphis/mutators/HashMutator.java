package com.mongodb.memphis.mutators;

import org.bson.BsonDocument;

public class HashMutator implements Mutator {

	@Override
	public void mutate(BsonDocument document) {
//		for (String hashIndex : config.getHashIndex()) {
//
//			BsonValue hashFieldValue = document.get(hashIndex);
//			if (hashFieldValue != null && hashFieldValue.isString()) {
//				String value = hashFieldValue.asString().getValue();
//
//				ByteBuffer buffer = ByteBuffer.wrap(DigestUtils.md5(value));
//				// get least-significant bits
//				buffer.position(buffer.limit() - 4);
//
//				document.put(hashIndex + config.getHashSuffix(), new BsonInt32(buffer.getInt()));
//			}
//		}
	}

}
