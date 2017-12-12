package com.mongodb.memphis.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.bson.BsonDocument;

import com.mongodb.memphis.placeholder.Placeholder;
import com.mongodb.memphis.placeholder.PlaceholderFactory;
import com.mongodb.memphis.placeholder.PlaceholderParser;
import com.mongodb.memphis.placeholder.location.PlaceholderLocation;

public class Template extends Config {
	private String templateFile;
	private String placeholderFile;
	private int weighting = 1;

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

	public BsonDocument cloneDocument() {
		return template.clone();
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

	public List<PlaceholderLocation> parseDocument(BsonDocument document) {
		return parser().parseDocument(document);
	}

	public java.util.Collection<Placeholder> getPlaceholders() {
		return parser().getPlaceholders();
	}

	private PlaceholderParser parser() {
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
			template = BsonDocument.parse(new String(Files.readAllBytes(templatePath)));
		}
		catch (IOException e) {
			logger.error("Could not parse template file {}", templatePath);
			throw new RuntimeException(e);
		}

		PlaceholderFactory.getInstance().load(placeholderFile);
	}

	@Override
	public void executeInternal() {
	}

}
