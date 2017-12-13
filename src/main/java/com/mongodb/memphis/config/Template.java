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

	private transient BsonDocument referenceDocument;
	private transient Integer documentSize;

	public final String getTemplateFile() {
		return templateFile;
	}

	public final int getWeighting() {
		return weighting;
	}

	public final BsonDocument getReferenceDocument() {
		return referenceDocument;
	}

	public void setPlaceholderFile(String placeholderFile) {
		this.placeholderFile = placeholderFile;
	}

	public void setWeighting(int weighting) {
		this.weighting = weighting;
	}

	public void setTemplate(BsonDocument template) {
		this.referenceDocument = template;
	}

	public Integer getDocumentSize() {
		return documentSize;
	}

	public void setDocumentSize(int documentSize) {
		this.documentSize = documentSize;
	}

	public PlaceholderParser getPlaceholderParser() {
		return PlaceholderFactory.getInstance().getParser(placeholderFile);
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
			referenceDocument = BsonDocument.parse(new String(Files.readAllBytes(templatePath)));
		}
		catch (IOException e) {
			logger.error("Could not parse template file {}", templatePath);
			throw new RuntimeException(e);
		}

		PlaceholderFactory.getInstance().load(placeholderFile);
	}

	@Override
	protected void executeInternal() {
	}

}
