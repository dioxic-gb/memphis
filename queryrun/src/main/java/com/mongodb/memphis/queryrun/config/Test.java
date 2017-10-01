package com.mongodb.memphis.queryrun.config;

import java.util.List;

public class Test extends Config {

	private String name;
	private List<Stage> stages;

	public String getName() {
		return name;
	}

	public List<Stage> getStages() {
		return stages;
	}

	@Override
	public void execute() {
		for (Stage stage : stages) {
			stage.execute();
		}
	}

	@Override
	public List<Stage> getChildren() {
		return stages;
	}

}
