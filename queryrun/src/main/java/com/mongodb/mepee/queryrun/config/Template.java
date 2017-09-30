package com.mongodb.mepee.queryrun.config;

public class Template {
	private String templateFile;
	private String placeholderFile;
	private int weighting;

	public final String getTemplateFile() {
		return templateFile;
	}

	public final String getPlaceholderFile() {
		return placeholderFile;
	}

	public final int getWeighting() {
		return weighting;
	}

	@Override
	public String toString() {
		return "Template [templateFile=" + templateFile + ", placeholderFile=" + placeholderFile + ", weighting=" + weighting + "]";
	}

}
