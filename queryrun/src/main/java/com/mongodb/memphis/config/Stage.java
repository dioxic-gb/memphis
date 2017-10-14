package com.mongodb.memphis.config;

import java.util.Arrays;
import java.util.List;

import com.mongodb.memphis.operations.Operation;

public class Stage extends Config {
	private String name;
	private Operation operation;

	public String getName() {
		return name;
	}

	public Operation getOperation() {
		return operation;
	}

	@Override
	public void execute() {
		if (operation != null) {
			operation.execute();
		}
		else {
			logger.warn("No operations to run for stage [{}]", name);
		}
	}

	@Override
	public List<Operation> getChildren() {
		return operation != null ? Arrays.asList(operation) : null;
	}

}
