package com.mongodb.memphis.engine;

import com.mongodb.memphis.config.Root;
import com.mongodb.memphis.config.Test;

public class TestEngine {

	private final Root config;

	public TestEngine(Root config) {
		this.config = config;
	}

	public void run() {
		for (Test test : config.getTests()) {
			test.execute();
		}
	}
}
