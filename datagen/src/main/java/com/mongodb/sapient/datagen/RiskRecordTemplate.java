package com.mongodb.sapient.datagen;


import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class RiskRecordTemplate {
    Logger logger;
    String templateFilenameScalar;
    String templateFilename1D;
    String templateFilename2D;
    Document templateDocumentScalar = new Document();
    Document templateDocument1D = new Document();
    Document templateDocument2D = new Document();
    Document generatedValues = null;
    ValueGen valueGen;
    // final static String hardCodedPath = "D:\\Newfolder\\ScalingTest\\templates";


    List riskType2DList = Arrays.asList("Risk1", "Risk2", "Risk3");
    List riskType1DList = Arrays.asList("Risk4", "Risk5", "Risk6", "Risk7", "Risk8", "Risk9", "Risk10", "Risk11", "Risk12", "Risk13");
    List riskTypeScalarList = Arrays.asList("Risk14", "Risk15", "Risk16", "Risk17", "Risk18", "Risk19", "Risk20", "Risk21", "Risk22", "Risk23", "Risk24", "Risk25");


    RiskRecordTemplate(String templatePath, ValueGen valueGen) {
        logger = LoggerFactory.getLogger(RiskRecordTemplate.class);
        this.valueGen = valueGen;
        templateFilenameScalar = templatePath + "/" + "risk-1D-schema.json";
        templateFilename1D = templatePath + "/" + "risk-2D-schema.json";
        templateFilename2D = templatePath + "/" + "risk-scalar-schema.json";
        try {
            String templateJsonScalar = new String(Files.readAllBytes(Paths.get(templateFilenameScalar)),
                    StandardCharsets.UTF_8);
            String templateJson1D = new String(Files.readAllBytes(Paths.get(templateFilename1D)),
                    StandardCharsets.UTF_8);
            String templateJson2D = new String(Files.readAllBytes(Paths.get(templateFilename2D)),
                    StandardCharsets.UTF_8);

            templateDocumentScalar = Document.parse(templateJsonScalar);
            templateDocument1D = Document.parse(templateJson1D);
            templateDocument2D = Document.parse(templateJson2D);
        } catch (IOException e) {
            logger.error(e.getMessage());
            System.exit(1);
        }
    }

    public List<Document> getExampleDocument(int shard, String valuationDate, String hsbcTradeId, String tradeId, String validFromTimeStamp) {
        return getExampleRiskDocument(null, valuationDate, shard, hsbcTradeId, tradeId, validFromTimeStamp);
    }


    public List<Document> getExampleRiskDocument(Document root, String valuationDate, int shard, String tradeId, String hsbcTradeId, String validFromTimeStamp) {
        List<Document> documents = new ArrayList<>();

        for (int i = 0; i < riskTypeScalarList.size(); i++) {
            documents.add(getExampleDocument(root, shard, valuationDate, hsbcTradeId, tradeId, riskTypeScalarList.get(i).toString(), validFromTimeStamp, templateDocumentScalar));
        }
        for (int i = 0; i < riskType1DList.size(); i++) {
            documents.add(getExampleDocument(root, shard, valuationDate, hsbcTradeId, tradeId, riskTypeScalarList.get(i).toString(), validFromTimeStamp, templateDocument1D));
        }
        for (int i = 0; i < riskType2DList.size(); i++) {
            documents.add(getExampleDocument(root, shard, valuationDate, hsbcTradeId, tradeId, riskTypeScalarList.get(i).toString(), validFromTimeStamp, templateDocument2D));
        }

        return documents;

    }


    private Document getExampleDocument(Document root, int shard, String valuationDate, String hsbcTradeId, String tradeId, String riskType, String validFromTimeStamp, Document templateDocument) {


        Document rval = new Document();
        if (root == null) {
            root = templateDocument;
            rval.append("_id", new Document("s", shard).append("i", new ObjectId()));
            generatedValues = new Document(); //Keeps track of used feields
        }

        Set<String> keys = root.keySet();

        for (String key : keys) {

            Object value = root.get(key);
            //logger.info(value.getClass().toString());
            if (value instanceof ArrayList) {

                ArrayList<Object> al = new ArrayList<Object>();
                ArrayList<Object> v = (ArrayList<Object>) value;
                for (Integer i = 0; i < v.size(); i++) {
                    if (v.get(i) instanceof Document) {
                        al.add(getExampleDocument((Document) v.get(i), shard, valuationDate, tradeId, hsbcTradeId, riskType, validFromTimeStamp, templateDocument));
                    } else {
                        if (v.get(i) instanceof String) {
                            String valuetext = (String) v.get(i);
                            if (valuetext.startsWith("${")) {
                                String lookupname = valuetext.substring(2, valuetext.length() - 1);


                                Object subst;

                                subst = generatedValues.get(lookupname);

                                if (subst == null) {
                                    String basename = lookupname.replaceAll("\\d*$", "");
                                    basename = basename.replaceAll("\\d*\\.", ".");
                                    //Remove trailing digits
                                    subst = valueGen.getValue(valuationDate, basename, hsbcTradeId, tradeId, riskType, validFromTimeStamp);
                                }

                                if (subst == null) {
                                    logger.error("CRITICAL - CANNOT GET A VALUE FOR " + lookupname);
                                } else {
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
            } else if (value instanceof Document) {
                rval.append(key, getExampleDocument((Document) value, shard, valuationDate, tradeId, hsbcTradeId, riskType, validFromTimeStamp, templateDocument));
            } else {
                //Just copy
                if (value instanceof String) {
                    String valuetext = (String) value;
                    if (valuetext.startsWith("${")) {
                        String lookupname = valuetext.substring(2, valuetext.length() - 1);


                        Object subst;

                        subst = generatedValues.get(lookupname);

                        if (subst == null) {
                            String basename = lookupname.replaceAll("\\d*$", "");
                            basename = basename.replaceAll("\\d*\\.", ".");
                            //Remove trailing digits
                            subst = valueGen.getValue(valuationDate, basename, hsbcTradeId, tradeId, riskType, validFromTimeStamp);
                        }

                        if (subst == null) {
                            logger.error("CRITICAL - CANNOT GET A VALUE FOR " + lookupname);
                        } else {
                            //logger.info("Got a value for " + lookupname+ " = "+subst);

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
