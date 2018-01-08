package com.mongodb.memphis.config;

import java.util.Arrays;
import java.util.List;

import com.mongodb.memphis.operations.Operation;

public class Stage extends Config {

	private Operation operation;
	private boolean disabled = false;

	public Operation getOperation() {
		return operation;
	}

	public boolean isDisabled() {
		return disabled;
	}

	@Override
	public void executeInternal() {
		if (operation != null) {
			if (!disabled) {
				operation.execute();
			}
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
