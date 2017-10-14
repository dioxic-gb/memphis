package com.mongodb.memphis.operations;

import com.mongodb.memphis.config.Config;
import com.mongodb.memphis.util.StringUtils;

public abstract class Operation extends Config {

	@Override
	public void execute() {
		logger.info("Operation {} starting", getClass().getSimpleName());

		long startTime = System.currentTimeMillis();

		executeInternal();

		long totalTime = System.currentTimeMillis() - startTime;

		logger.info("Operation {} completed in {}", getClass().getSimpleName(), StringUtils.prettifyTime(totalTime));
	}

	protected abstract void executeInternal();

}