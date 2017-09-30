package com.mongodb.mepee.datagen;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecordTemplate {
	Logger logger;
	String templateFilename;
	Document templateDocument = new Document();
	Document generatedValues = null;
	ValueGen valueGen;
	String templatePath;
	List<String> hashIndex;
	List<String> epocIndex;
	MessageDigest md5;
	DateTimeFormatter dtf = DateTimeFormatter.ISO_INSTANT;

	RecordTemplate(String template, List<String> hashIndex, List<String> epocIndex, ValueGen valueGen) {
		logger = LoggerFactory.getLogger(RecordTemplate.class);
		this.valueGen = valueGen;
		this.hashIndex = hashIndex;
		this.epocIndex = epocIndex;
		templatePath = valueGen.getTemplatePath();
		templateFilename = templatePath + "/" + template + ".json";

		try {
			md5 = MessageDigest.getInstance("MD5");
			String templateJson = new String(Files.readAllBytes(Paths.get(templateFilename)),
					StandardCharsets.UTF_8);

			templateDocument = Document.parse(templateJson);
		}
		catch (IOException e) {
			logger.error(e.getMessage());
			System.exit(1);
		}
		catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
			System.exit(1);
		}
	}

	Document getTemplateDoc() {
		return templateDocument;
	}

	public Document getExampleDocument(int shard) {
		Document d = getExampleDocument(null, shard);
		Document indexDoc = new Document();

		Set<String> keys = generatedValues.keySet();

		for (String key : keys) {
			Object generatedValue = generatedValues.get(key);
			key = key.replace(".", "");
			indexDoc.append(key, generatedValue);
			if (hashIndex != null && hashIndex.contains(key)) {
				// assuming all hashIndexes are strings
				md5.reset();
				byte[] hash = md5.digest(generatedValue.toString().getBytes());
				indexDoc.append(key + "Hash", ByteBuffer.wrap(hash).getInt());
			}
			if (epocIndex != null && epocIndex.contains(key)) {
				// assuming all dates are strings
				String value = generatedValue.toString();
				long epoch = 0l;
				if (value.length() > 10) {
					epoch = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(generatedValue.toString())).toEpochMilli();
				}
				else {
					epoch = LocalDate.from(DateTimeFormatter.ISO_DATE.parse(generatedValue.toString())).toEpochDay();
				}

				indexDoc.append(key + "Epoc", (int)epoch);
			}
		  if( key.equals("tradetradeId" )){
			  String tradeId = generatedValue.toString();
			  indexDoc.append("tradebookId", tradeId.substring(tradeId.length()-4));
		  }
		}
		d.append("index", indexDoc);
		return d;
	}

	private Document getExampleDocument(Document root, int shard) {

		Document rval = new Document();
		if (root == null) {
			root = templateDocument;
			rval.append("_id", new Document("s", shard).append("i", new ObjectId()));
			generatedValues = new Document(); // Keeps track of used feields
		}

		Set<String> keys = root.keySet();

		for (String key : keys) {

			Object value = root.get(key);
			// logger.info(value.getClass().toString());
			if (value instanceof java.util.ArrayList) {

				ArrayList<Object> al = new ArrayList<>();
				ArrayList<Object> v = (ArrayList<Object>) value;
				for (Integer i = 0; i < v.size(); i++) {
					if (v.get(i) instanceof Document) {
						al.add(getExampleDocument((Document) v.get(i), shard));
					}
					else {
						if (v.get(i) instanceof String) {
							String valuetext = (String) v.get(i);
							if (valuetext.startsWith("${")) {
								String lookupname = valuetext.substring(2, valuetext.length() - 1);

								logger.info(lookupname);
								Object subst;

								subst = generatedValues.get(lookupname);

								if (subst == null) {
									String basename = lookupname.replaceAll("\\d*$", "");
									basename = basename.replaceAll("\\d*\\.", ".");
									// Remove trailing digits
									subst = valueGen.getValue(basename);
									logger.info("generated " + subst);
								}

								if (subst == null) {
									logger.error("CRITICAL - CANNOT GET A VALUE FOR " + lookupname);
									System.exit(1);
								}
								else {
									logger.info("Got a value for " + lookupname + " = " + subst);

									value = subst;
									generatedValues.append(lookupname, value);

								}
							}
						}
						al.add(value);
					}

				}
				rval.append(key, al);
			}
			else if (value instanceof Document) {
				rval.append(key, getExampleDocument((Document) value, shard));
			}
			else {
				// Just copy
				if (value instanceof String) {
					String valuetext = (String) value;
					if (valuetext.startsWith("${")) {
						String lookupname = valuetext.substring(2, valuetext.length() - 1);

						Object subst;

						subst = generatedValues.get(lookupname);

						if (subst == null) {
							String basename = lookupname.replaceAll("\\d*$", "");
							basename = basename.replaceAll("\\d*\\.", ".");
							// Remove trailing digits
							subst = valueGen.getValue(basename);
						}

						if (subst == null) {
							logger.error("CRITICAL - CANNOT GET A VALUE FOR " + lookupname);
						}
						else {
							// logger.info("Got a value for " + lookupname+ " =
							// "+subst);

							value = subst;
							generatedValues.append(lookupname, value);

						}
					}
				}

				rval.append(key, value);
			}
		}

		return rval;
	}

}
