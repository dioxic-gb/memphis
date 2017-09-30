package com.mongodb.mepee.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StringUtils {

	public static List<String> parsePlaceholders(List<String> inputStrings, Map<String, String> placeholders) {
		List<String> output = new ArrayList<>(inputStrings.size());

		for (String input : inputStrings) {
			output.add(parsePlaceholder(input, placeholders));
		}

		return output;
	}

	public static String parsePlaceholder(String inputString, Map<String, String> placeholders) {
		String output = inputString;

		if (placeholders != null) {
			for (Map.Entry<String, String> entry : placeholders.entrySet()) {
				output = output.replace("${" + entry.getKey() + "}", entry.getValue());
			}
		}

		return output;
	}

	public static String prettifyTime(long milliseconds) {
		if (milliseconds > 1000) {
			milliseconds = milliseconds / 1000;
			if (milliseconds > 60) {
				return milliseconds / 60 + "m " + milliseconds % 60 + "s";
			}
			return milliseconds + "s";
		}
		return milliseconds + "ms";
	}

	public static String prettifyTransferRate(long bytesPerSecond) {
		return prettifySize(bytesPerSecond) + "/s";
	}

	public static String prettifySize(Long bytes) {
		double d = bytes.doubleValue();
		DecimalFormat df = new DecimalFormat("####.#");
		if (d > 1024) {
			d /= 1024;
			if (d > 1024) {
				d /= 1024;
				if (d > 1024) {
					d /= 1024;
					return df.format(d) + "GB";
				}
				return df.format(d)  + "MB";
			}
			return df.format(d)  + "kB";
		}
		return df.format(d)  + " bytes";
	}

}
