package com.mongodb.memphis.queryrun.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.bson.BsonDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.annotations.SerializedName;

public class Template extends Config {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private String templateFile;
	private int weighting = 1;

	@SerializedName("placeholderFile")
	private PlaceholderParser placeholderParser;
	private transient BsonDocument template;

	public final String getTemplateFile() {
		return templateFile;
	}

	public final int getWeighting() {
		return weighting;
	}

	public final BsonDocument getTemplate() {
		return template;
	}

	public final PlaceholderParser getPlaceHolderParser() {
		return placeholderParser;
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
	}

	@Override
	public List<? extends Config> getChildren() {
		return Arrays.asList(placeholderParser);
	}

	@Override
	public void execute() {
	}

}
