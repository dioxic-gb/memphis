package com.mongodb.sapient.datagen;


import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

//Returns an Example of X
//Version 1 works for single fields
public class ValueGen {
    Logger logger;
    // final String hardcodedPathName = "D:\\Newfolder\\ScalingTest\\templates";
    String listPath;
    HashMap<String, ArrayList<String>> lists;
    RngWrapper rng;

    public ValueGen(String templatePath) {
        logger = LoggerFactory.getLogger(ValueGen.class);
        listPath = templatePath + "/lists";
        lists = new HashMap<String, ArrayList<String>>();
        rng = new RngWrapper(); //Note - scope to make faster
        readLists();
    }

    //Reads them all - we can be smaarter

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
                        //This is a simple list of values or a list of one 'metavalue'
                        lists.put(fname.substring(0, fname.length() - 5), (ArrayList<String>) valuesDocument.get("list"));

                    }
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    System.exit(1);
                }

            }
        }
    }

    Object getValue(String valuationDate, String fieldname, String hsbcTradeId, String tradeId, String riskType, String validFromTimeStamp) {

        //logger.info("Looking for list for "+fieldname);

        if (fieldname.equals("HSBC_TRADE_ID")) {
            return hsbcTradeId;
        }

        if (fieldname.equals("TRADE_ID")) {
            return tradeId;
        }

        if (fieldname.equals("VALUATION_DATE")) {
            return Long.parseLong(valuationDate);
        }

        if (fieldname.equals("VALID_FROM")) {
            return Long.parseLong(valuationDate + validFromTimeStamp);
        }

        if (fieldname.equals("RISK_TYPE")) {
            return riskType;
        }

        if (fieldname.equals("BOOK_ID")) {
            return tradeId.substring(tradeId.length() - 4);
        }


        if (lists.containsKey(fieldname)) {
            int listlen = lists.get(fieldname).size();
            int choice = rng.generateInt(listlen);
            String value = (String) lists.get(fieldname).get(choice);

            Double numvalue = null;
            if (value.startsWith("@")) {
                if (value.equals("@dateTime")) {
                    value = rng.generateDateTime();
                } else if (value.equals("@date")) {
                    value = rng.generateDate();
                } else if (value.equals("@price")) {
                    numvalue = new Double(rng.generatePrice());
                } else if (value.equals("@tradeId")) {
                    value = rng.generateTradeId();
                } else if (value.equals("@messageId")) {
                    value = rng.generateTradeId() + UUID.randomUUID();
                    ;
                } else if (value.startsWith("@generateId(")) {
                    String params = value.substring(12);
                    params = params.substring(0, params.length() - 1);
                    String[] parts = params.split(",");
                    value = rng.generateId(parts[0], Integer.parseInt(parts[1]));
                } else if (value.startsWith("@measure_value")) {
                    numvalue = rng.generateMeasureValue();
                }
            }


            if (numvalue != null) {
                return numvalue;
            } else {
                return value;
            }
        }


        return null;
    }

}
