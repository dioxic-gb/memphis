package com.mongodb.memphis.queryrun.config;

public abstract class Named {

	private String name;

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

}
