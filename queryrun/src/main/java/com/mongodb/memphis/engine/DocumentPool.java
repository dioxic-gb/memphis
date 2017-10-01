package com.mongodb.memphis.engine;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.RawBsonDocument;
import org.bson.codecs.BsonDocumentCodec;

import com.mongodb.annotations.NotThreadSafe;
import com.mongodb.memphis.config.Template;

/**
 * Holds a pool of documents that can be reused in every batch run.
 *
 * @author Mark Baker-Munton
 */
@NotThreadSafe
public class DocumentPool extends AbstractDocumentPool {

	public DocumentPool(List<Template> templates, int poolSize) {
		super(templates, poolSize);
	}

	/**
	 * Calculates the average document size in the pool. Only one document per template is evaluated to avoid overhead with object creation.
	 * <p>
	 * The calculation is performed only once and the result is cached internally.
	 * <p>
	 * This should be called after the placeholder values have been applied for the results to be accurate.
	 * @return average document size in bytes
	 */
	public long getAverageDocumentSize() {
		if (averageDocumentSize == -1) {
			averageDocumentSize = Arrays.stream(templateExamples).collect(Collectors.averagingLong(d -> {
				return new RawBsonDocument(d, new BsonDocumentCodec()).getByteBuffer().limit();
			})).longValue();
		}
		return averageDocumentSize;
	}

}
