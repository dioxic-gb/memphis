package com.mongodb.mepee.datagen;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Returns an Example of X
//Version 1 works for single fields
public class ValueGen {
	Logger logger;
	String pathName;
	String listPath;
	HashMap<String, ArrayList<String>> lists;
	RngWrapper rng;

	String getTemplatePath() {
		return pathName;
	}

	ValueGen(String path) {
		pathName = path;
		logger = LoggerFactory.getLogger(ValueGen.class);
		listPath = pathName + "/lists";
		lists = new HashMap<>();
		rng = new RngWrapper(); // Note - scope to make faster
		readLists();
	}

	// Reads them all - we can be smaarter

	private void readLists() {
		File[] files = new File(listPath).listFiles();
		for (File file : files) {
			logger.info(file.getName());
			String fname = file.getName();
			if (fname.endsWith(".json")) {
				try {
					String valueJson = new String(Files.readAllBytes(Paths.get(file.getPath())),
							StandardCharsets.UTF_8);

					Document valuesDocument = Document.parse(valueJson);
					if (valuesDocument.containsKey("list")) {
						// This is a simple list of values or a list of one
						// 'metavalue'
						lists.put(fname.substring(0, fname.length() - 5), (ArrayList<String>) valuesDocument.get("list"));

					}
				}
				catch (IOException e) {
					logger.error(e.getMessage());
					System.exit(1);
				}

			}
		}
	}

	Object getValue(String fieldname) {
		// logger.info("Looking for list for "+fieldname);
		if (lists.containsKey(fieldname)) {
			int listlen = lists.get(fieldname).size();
			int choice = rng.generateInt(listlen);
			String value = lists.get(fieldname).get(choice);
			Object result = value;

			if (value.startsWith("@")) {
				if (value.equals("@dateTime")) {
					result = rng.generateDateTime();
				}
				else if (value.equals("@date")) {
					result = rng.generateDate();
				}
				else if (value.equals("@price")) {
					result = new Double(rng.generatePrice());
				}
				else if (value.equals("@tradeId")) {
					result = rng.generateTradeId();
				}
				else if (value.equals("@epoch")) {
					result = rng.generateEpoch();
				}
				else if (value.equals("@messageId")) {
					result = rng.generateTradeId() + UUID.randomUUID();
				}
				else if (value.startsWith("@generateId(")) {
					String params = value.substring(12);
					params = params.substring(0, params.length() - 1);
					String[] parts = params.split(",");
					result = rng.generateId(parts[0], Integer.parseInt(parts[1]));
				}
			}

			return result;
		}
		return null;
	}
}
