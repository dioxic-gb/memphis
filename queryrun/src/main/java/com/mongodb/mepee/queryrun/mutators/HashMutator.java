package com.mongodb.mepee.queryrun.mutators;

import java.nio.ByteBuffer;

import org.apache.commons.codec.digest.DigestUtils;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonValue;
import org.bson.Document;

import com.mongodb.mepee.queryrun.Config;
import com.mongodb.mepee.queryrun.query.Query;

public class HashMutator implements Mutator {

	private final Config config;

	public HashMutator(Config config) {
		this.config = config;
	}

	/* (non-Javadoc)
	 * @see com.mongodb.johnlpage.scalingtest.queryrun.mutators.QueryMutator#mutate(com.mongodb.johnlpage.scalingtest.queryrun.query.QueryTemplate)
	 */
	@Override
	public void mutate(BsonDocument document) {
		for (String hashIndex : config.getHashIndex()) {
			
			BsonValue hashFieldValue = document.get(hashIndex);
			if (hashFieldValue != null && hashFieldValue.isString()) {
				String value = hashFieldValue.asString().getValue();
				
				ByteBuffer buffer = ByteBuffer.wrap(DigestUtils.md5(value));
				// get least-significant bits
				buffer.position(buffer.limit() - 4);
				
				document.put(hashIndex + config.getHashSuffix(), new BsonInt32(buffer.getInt()));
			}
		}
	}

	@Override
	public String toString() {
		return "HashMutator [hashSuffix=" + config.getHashSuffix() + ", hashFields=" + config.getHashIndex() + "]";
	}
	
}
