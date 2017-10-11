package com.mongodb.memphis;

import java.io.IOException;
import java.util.logging.LogManager;

import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.mongodb.memphis.config.Root;

public class Application {
	
	private static CommandLineOptions options;
	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		LogManager.getLogManager().reset();

		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();

		try {
			options = new CommandLineOptions(args);
		} catch (ParseException e) {
			logger.error("Failed to parse command line options");
			logger.error(e.getMessage());
			System.exit(1);
		}
		
		try {
			Root root = options.getConfig();
			root.initialise();
			root.execute();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

}
