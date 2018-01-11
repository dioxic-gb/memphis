package com.mongodb.memphis.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.bson.BsonDocument;

import com.mongodb.memphis.placeholder.PlaceholderFile;
import com.mongodb.memphis.util.FileUtil;

public class Template extends Config {
	private String templateFile;
	private PlaceholderFile placeholderFile;
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

	public PlaceholderFile getPlaceholderFile() {
		return placeholderFile;
	}

	@Override
	public void initialise() {
		Path templatePath = FileUtil.resolveFile(templateFile);

		try {
			referenceDocument = BsonDocument.parse(new String(Files.readAllBytes(templatePath)));
		}
		catch (IOException e) {
			logger.error("Could not parse template file {}", templatePath);
			throw new RuntimeException(e);
		}

		//PlaceholderFactory.getInstance().loadFromFile(placeholderFile);
	}

	public boolean hasId() {
		return referenceDocument.containsKey("_id");
	}

	@Override
	protected void executeInternal() {
	}

}
