package com.mongodb.memphis.operation;

import org.apache.commons.lang3.Validate;

import com.mongodb.memphis.config.Config;

public abstract class Operation extends Config {

	protected void validate() {
		Validate.notNull(getCollection(), "Collection not set for %s", this.getClass().getSimpleName());
	}

}