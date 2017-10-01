package com.mongodb.memphis.queryrun.engine;

import com.mongodb.memphis.queryrun.config.Root;
import com.mongodb.memphis.queryrun.config.Test;

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
