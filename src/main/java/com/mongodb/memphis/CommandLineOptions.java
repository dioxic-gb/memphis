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

import com.mongodb.memphis.config.Filter;
import com.mongodb.memphis.config.Root;

public class CommandLineOptions {
	private final boolean helpOnly = false;
	private final Logger logger;
	private String configFile;
	private boolean debug;
	private boolean trace;
	private Filter filter;

	public CommandLineOptions(String[] args) throws ParseException {
		logger = LoggerFactory.getLogger(CommandLineOptions.class);
		logger.info("Parsing Command Line");

		CommandLineParser parser = new DefaultParser();

		Options cliopt;
		cliopt = new Options();

		cliopt.addOption("h", "help", false, "Show Help");
		cliopt.addOption("c", "config", true, "config file");
		cliopt.addOption("D", "debug", false, "debug outputs");
		cliopt.addOption("T", "trace", false, "trace outputs");
		cliopt.addOption("t", "test", true, "test filter");
		cliopt.addOption("s", "stage", true, "stage filter");
		cliopt.addOption("o", "operation", true, "operation filter");

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

		if (cmd.hasOption("D")) {
			debug = true;
		}

		if (cmd.hasOption("T")) {
			trace = true;
		}

		filter = new Filter(cmd.getOptionValue("t"), cmd.getOptionValue("s"), cmd.getOptionValue("o"));

	}

	public boolean isDebug() {
		return debug;
	}

	public boolean isTrace() {
		return trace;
	}

	public Root getConfig() throws IOException {
		Root root = Root.loadFromFile(configFile);
		root.setFilter(filter);
		return root;
	}

}
