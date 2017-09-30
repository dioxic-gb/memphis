package com.mongodb.mepee.queryrun;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bson.BsonDocument;

import com.mongodb.annotations.NotThreadSafe;
import com.mongodb.mepee.queryrun.placeholder.Placeholder;
import com.mongodb.mepee.queryrun.placeholder.PlaceholderParser;
import com.mongodb.mepee.queryrun.placeholder.PlaceholderParser.PlaceHolderLocation;

/**
 * Holds a pool of documents that can be reused in every batch run.
 * 
 * @author Mark Baker-Munton
 */
@NotThreadSafe
public class ThreadBsonDocumentPool {

	private int poolSize;
	private BsonDocument templateDoc;
	private PlaceholderParser parser;
	private List<BsonDocument> documents;
	private List<PlaceHolderLocation> placeHolderLocations;
	private Collection<Placeholder> placeholders;

	private ThreadBsonDocumentPool() {}
	
	private void initialise() {
		documents = new ArrayList<>(poolSize);
		placeHolderLocations = new ArrayList<>(poolSize);

		// initialise doc list
		for (int i = 0; i < poolSize; i++) {
			documents.add(templateDoc.clone());
			placeHolderLocations.addAll(parser.parseDocument(documents.get(i)));
		}
		placeholders = parser.getGenerators();
	}

	public List<BsonDocument> getDocuments() {
		return Collections.unmodifiableList(documents);
	}
	
	/**
	 * Regenerates all the placeholder values of all records.
	 * 
	 * Called prior to executing an operation. 
	 */
	public void regenerateValues() {
		for (Placeholder placeholder : placeholders) {
			placeholder.nextBatch();
		}
		for (PlaceHolderLocation locator : placeHolderLocations) {
			locator.apply();
		}
	}
	
	public static ThreadBsonDocumentPoolBuilder builder() {
		return new ThreadBsonDocumentPoolBuilder();
	}

	static class ThreadBsonDocumentPoolBuilder {
		private int poolSize;
		private BsonDocument templateDoc;
		private PlaceholderParser parser;

		public ThreadBsonDocumentPoolBuilder poolSize(int poolSize) {
			this.poolSize = poolSize;
			return this;
		}

		public ThreadBsonDocumentPoolBuilder template(BsonDocument templateDoc) {
			this.templateDoc = templateDoc;
			return this;
		}

		public ThreadBsonDocumentPoolBuilder placeholderParser(PlaceholderParser parser) {
			this.parser = parser;
			return this;
		}

		public ThreadBsonDocumentPool build() {
			ThreadBsonDocumentPool pool = new ThreadBsonDocumentPool();
			pool.poolSize = poolSize;
			pool.templateDoc = templateDoc;
			pool.parser = parser;
			pool.initialise();
			return pool;
		}
	}

}
