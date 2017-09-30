package com.mongodb.mepee.queryrun.config;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;

public class Collection {

	private WriteConcern writeConcern;
	private ReadConcern readConcern;
	private ReadPreference readPreference;

	public final WriteConcern getWriteConcern() {
		return writeConcern;
	}

	public final ReadConcern getReadConcern() {
		return readConcern;
	}

	public final ReadPreference getReadPreference() {
		return readPreference;
	}

}
