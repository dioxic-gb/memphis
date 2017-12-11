package com.mongodb.memphis.config;

import java.util.List;

public class Test extends Config {

	private List<Stage> stages;

	public List<Stage> getStages() {
		return stages;
	}

	@Override
	public void executeInternal() {
		for (Stage stage : stages) {
			stage.execute();
		}
	}

	@Override
	public List<Stage> getChildren() {
		return stages;
	}

}
