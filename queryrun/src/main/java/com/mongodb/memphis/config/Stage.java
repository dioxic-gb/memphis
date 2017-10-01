package com.mongodb.memphis.config;

import java.util.Arrays;
import java.util.List;

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
		operation.execute();
	}

	@Override
	public List<Operation> getChildren() {
		return Arrays.asList(operation);
	}

}
