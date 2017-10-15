package com.mongodb.memphis;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.memphis.config.Root;

public class CommandLineOptions {
	private final boolean helpOnly = false;
	private final Logger logger;
	private String configFile;
	private boolean debug;
	private boolean trace;

	public CommandLineOptions(String[] args) throws ParseException {
		logger = LoggerFactory.getLogger(CommandLineOptions.class);
		logger.info("Parsing Command Line");

		CommandLineParser parser = new DefaultParser();

		Options cliopt;
		cliopt = new Options();

		cliopt.addOption("h", "help", false, "Show Help");
		cliopt.addOption("c", "config", true, "config file");
		cliopt.addOption("d", "debug", false, "debug outputs");
		cliopt.addOption("t", "trace", false, "trace outputs");

		CommandLine cmd = parser.parse(cliopt, args);

		if (cmd.hasOption("c")) {
			configFile = cmd.getOptionValue("c");
		} else {
			logger.error("No config file supplied - exiting");
			System.exit(1);
		}

		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Memphis", cliopt);
			System.exit(0);
		}

		if (cmd.hasOption("d")) {
			debug = true;
		}

		if (cmd.hasOption("t")) {
			trace = true;
		}
	}

	public boolean isDebug() {
		return debug;
	}

	public boolean isTrace() {
		return trace;
	}

	public Root getConfig() throws IOException {
		return Root.loadFromFile(configFile);
	}

}
