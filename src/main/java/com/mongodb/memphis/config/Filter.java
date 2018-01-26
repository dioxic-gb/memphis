package com.mongodb.memphis.config;

import com.mongodb.memphis.operation.Operation;

public class Filter {

	private String test;
	private String stage;
	private String operation;

	public Filter(String test, String stage, String operation) {
		this.test = test;
		this.stage = stage;
		this.operation = operation;
	}

	public boolean accept(Config config) {

		if (test != null && config instanceof Test) {
			return config.getName().matches(test);
		}
		else if (stage != null && config instanceof Stage) {
			return config.getName().matches(stage);
		}
		else if (operation != null && config instanceof Operation) {
			return config.getName().matches(operation);
		}

		return true;
	}
}
