package com.mongodb.mepee.queryrun;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandLineOptions {
	private final boolean helpOnly = false;
	private final Logger logger;
	private String configFile;
	private Config config;

	public CommandLineOptions(String[] args) throws ParseException {
		logger = LoggerFactory.getLogger(CommandLineOptions.class);
		logger.info("Parsing Command Line");

		CommandLineParser parser = new DefaultParser();

		Options cliopt;
		cliopt = new Options();

		cliopt.addOption("h", "help", false, "Show Help");
		cliopt.addOption("c", "config", true, "config file");
		cliopt.addOption("t", "config", true, "template file or folder");
		cliopt.addOption("d", "debug", false, "debug outputs");

		CommandLine cmd = parser.parse(cliopt, args);

		if (cmd.hasOption("c")) {
			configFile = cmd.getOptionValue("c");
		}

		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("WeatherService", cliopt);
			System.exit(0);
		}

		if (configFile != null) {
			try {
				config = Config.load(configFile);

				if (cmd.hasOption("t")) {
					config.setTemplates(cmd.getOptionValue("t"));
				}

				if (cmd.hasOption("d")) {
					config.setDebug(true);
				}

			} catch (IOException e) {
				logger.error("Error reading config file", e);
				System.exit(1);
			}
		} else {
			logger.error("No config file supplied - exiting");
			System.exit(1);
		}
	}

	public Config getConfig() {
		return config;
	}

}
