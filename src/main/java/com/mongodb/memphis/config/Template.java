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
		Path templatePath = resolveFile(templateFile);

		try {
			referenceDocument = BsonDocument.parse(new String(Files.readAllBytes(templatePath)));
		}
		catch (IOException e) {
			logger.error("Could not parse template file {}", templatePath);
			throw new RuntimeException(e);
		}

		PlaceholderFactory.getInstance().loadFromFile(placeholderFile, resolveFile(placeholderFile));
	}

	public boolean hasId() {
		return referenceDocument.containsKey("_id");
	}

	@Override
	protected void executeInternal() {
	}

	private Path resolveFile(String filename) {
		Path path = Paths.get(filename);

		if (!Files.exists(path)) {
			// try file relative to config file
			path = Paths.get(getRoot().getConfigFilePath().toString(), filename);

			if (!Files.exists(path)) {
				throw new IllegalStateException(path.toString() + " cannot be found!");
			}
		}
		if (Files.isDirectory(path)) {
			throw new IllegalStateException(path.toString() + " is a directory!");
		}

		return path;
	}

}
