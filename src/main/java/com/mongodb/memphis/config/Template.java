package com.mongodb.memphis.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.bson.BsonDocument;

import com.mongodb.memphis.placeholder.PlaceholderParser;
import com.mongodb.memphis.placeholder.PlaceholderParserFactory;
import com.mongodb.memphis.util.FileUtil;

public class Template extends Config {
	private static final String DEFAULT_TEMPLATE_FILE = "template.json";
	private static final String DEFAULT_PLACEHOLDER_FILE = "placeholders.json";

	// json config field
	private String templateFile = DEFAULT_TEMPLATE_FILE;
	private String placeholderFile = DEFAULT_PLACEHOLDER_FILE;
	private int weighting = 1;

	private transient PlaceholderParser parser;
	private transient BsonDocument referenceDocument;
	private transient Integer documentSize;

	public Template(String templateFile, String placeholderFile) {
		if (templateFile != null) {
			this.templateFile = templateFile;
		}
		if (placeholderFile != null) {
			this.placeholderFile = placeholderFile;
		}
		this.initialise();
	}

	public Template() {

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

	public PlaceholderParser getPlaceholderFile() {
		return parser;
	}

	@Override
	public void initialise() {
		Path templatePath = FileUtil.resolveFile(templateFile);
		this.parser = PlaceholderParserFactory.getInstance().loadFromFile(placeholderFile);

		try {
			referenceDocument = BsonDocument.parse(new String(Files.readAllBytes(templatePath)));
		}
		catch (IOException e) {
			logger.error("Could not parse template file {}", templatePath);
			throw new RuntimeException(e);
		}
	}

	public boolean hasId() {
		return referenceDocument.containsKey("_id");
	}

	@Override
	protected void executeInternal() {
	}

	public static Template getDefault() {
		return new Template(DEFAULT_TEMPLATE_FILE, DEFAULT_PLACEHOLDER_FILE);
	}

}
