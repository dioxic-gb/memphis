package com.mongodb.memphis.queryrun;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.memphis.queryrun.config.Root;

public class CommandLineOptions {
	private final boolean helpOnly = false;
	private final Logger logger;
	private String configFile;
	private Root config;
	private boolean debug;

	public CommandLineOptions(String[] args) throws ParseException {
		logger = LoggerFactory.getLogger(CommandLineOptions.class);
		logger.info("Parsing Command Line");

		CommandLineParser parser = new DefaultParser();

		Options cliopt;
		cliopt = new Options();

		cliopt.addOption("h", "help", false, "Show Help");
		cliopt.addOption("c", "config", true, "config file");
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
			config = Root.load(configFile);

			if (cmd.hasOption("d")) {
				debug = true;
			}
		}
		else {
			logger.error("No config file supplied - exiting");
			System.exit(1);
		}
	}

	public boolean isDebug() {
		return debug;
	}

	public Root getConfig() {
		return config;
	}

}