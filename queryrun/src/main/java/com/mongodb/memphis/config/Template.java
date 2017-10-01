package com.mongodb.memphis.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.bson.BsonDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.memphis.placeholder.Placeholder;
import com.mongodb.memphis.placeholder.PlaceholderFactory;
import com.mongodb.memphis.placeholder.PlaceholderParser;

public class Template extends Config {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Pattern pattern = Pattern.compile("\\$\\{(.+)\\}");

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
		return placeholderFactory.create();
	}

	@Override
	public void execute() {
	}

	/**
	 * A class to bind data generators to a particular place in an existing
	 * document.
	 *
	 * Used to avoid having to create a new object for every document (we can reuse
	 * objects for efficiency).
	 *
	 * @author Mark Baker-Munton
	 */
	public static class PlaceHolderLocation {
		BsonDocument document;
		String key;
		Placeholder placeholder;

		public PlaceHolderLocation(BsonDocument document, String key, Placeholder placeholder) {
			this.document = document;
			this.key = key;
			this.placeholder = placeholder;
		}

		public void apply() {
			document.put(key, placeholder.getValue());
		}

		public Placeholder getPlaceholder() {
			return placeholder;
		}

		@Override
		public String toString() {
			return "PlaceHolderLocation [key=" + key + ", value=" + placeholder.toString() + "]";
		}
	}

}
