package com.mongodb.mepee.queryrun.config;

import java.util.List;

public class Test {

	private String name;
	private List<Stage> stages;

	public String getName() {
		return name;
	}

	public List<Stage> getStages() {
		return stages;
	}

	@Override
	public String toString() {
		return "Test [name=" + name + ", stages=" + stages + "]";
	}

}
