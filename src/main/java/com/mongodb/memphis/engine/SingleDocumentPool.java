package com.mongodb.memphis.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.RawBsonDocument;
import org.bson.codecs.BsonDocumentCodec;

import com.mongodb.memphis.config.Template;

public class SingleDocumentPool extends AbstractDocumentPool  {

	private int currentDocumentIndex = -1;
	private Map<BsonDocument, Long> documentSizes;

	public SingleDocumentPool(List<Template> templates) {
		super(templates, templates.size());
		documentSizes = new HashMap<>(templates.size());
	}

	public BsonDocument getNextDocument() {
		currentDocumentIndex++;

		if (currentDocumentIndex == poolSize) {
			currentDocumentIndex = 0;
		}

		return getCurrentDocument();
	}

	public BsonDocument getCurrentDocument() {
		return documents.get(currentDocumentIndex);
	}

	/**
	 * Calculates the current document size (i.e. the document last returned by the GetNextDocument method.
	 * <p>
	 * Caches document size internally to avoid overhead.
	 * <p>
	 * This should be called after the placeholder values have been applied for the results to be accurate.
	 * @return current document size in bytes
	 */
	public long getCurrentDocumentSize() {
		Long currentDocSize = documentSizes.get(getCurrentDocument());

		if (currentDocSize == null) {
			currentDocSize = new Long(new RawBsonDocument(getCurrentDocument(), new BsonDocumentCodec()).getByteBuffer().limit());
			documentSizes.put(getCurrentDocument(), currentDocSize);
		}

		return currentDocSize;
	}

}
