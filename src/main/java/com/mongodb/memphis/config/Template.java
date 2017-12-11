package com.mongodb.memphis.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.bson.BsonDocument;

import com.mongodb.memphis.placeholder.PlaceholderFactory;
import com.mongodb.memphis.placeholder.PlaceholderParser;

public class Template extends Config {
	private String templateFile;
	private String placeholderFile;
	private int weighting = 1;

	private transient BsonDocument template;
	private transient PlaceholderFactory placeholderFactory;

	public final String getTemplateFile() {
		return templateFile;
	}

	public final int getWeighting() {
		return weighting;
	}

	public final BsonDocument getTemplate() {
		return template;
	}

	public void setPlaceholderFile(String placeholderFile) {
		this.placeholderFile = placeholderFile;
	}

	public void setWeighting(int weighting) {
		this.weighting = weighting;
	}

	public void setTemplate(BsonDocument template) {
		this.template = template;
	}

	@Override
	public void initialise() {
		Path templatePath = Paths.get(templateFile);

		if (!Files.exists(templatePath)) {
			throw new IllegalStateException(templatePath.toString() + " cannot be found!");
		}
		if (Files.isDirectory(templatePath)) {
			throw new IllegalStateException(templatePath.toString() + " is a directory!");
		}

		try {
			template = BsonDocument.parse(new String(Files.readAllBytes(templatePath)));
		}
		catch (IOException e) {
			logger.error("Could not parse template file {}", templatePath);
			throw new RuntimeException(e);
		}

		placeholderFactory = PlaceholderFactory.load(placeholderFile);
	}

	public PlaceholderParser createPlaceholderParser() {
		return placeholderFactory != null ? placeholderFactory.create() : null;
	}

	@Override
	public void executeInternal() {
	}

}
