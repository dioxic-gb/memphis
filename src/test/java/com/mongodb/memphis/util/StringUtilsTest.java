package com.mongodb.memphis.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class StringUtilsTest {

	@Test
	public void parsePlaceholders() {
		Map<String, String> placeholders = new HashMap<>();
		placeholders.put("plaice-holder1", "value");
		placeholders.put("haddock", "value");

		List<String> testStrings = new ArrayList<>();
		testStrings.add("{ a: 12, b: \"halibut\", c: \"${haddock}\"");
		testStrings.add("{ a: 12, b: \"haddock\", c: \"${plaice-holder1}\"");

		String[] parsedStrings = StringUtils.parsePlaceholders(testStrings, placeholders).toArray(new String[0]);

		assertThat(parsedStrings[0]).isEqualTo("{ a: 12, b: \"halibut\", c: \"value\"");
		assertThat(parsedStrings[1]).isEqualTo("{ a: 12, b: \"haddock\", c: \"value\"");

	}

	@Test
	public void prettifySize() {
		long bytes = 460L;
		long kbytes = 598968L;  // 584.93
		long mbytes = 450698936L; // 429.82
		long gbytes = 3886945403L; // 3.63

		assertThat(StringUtils.prettifySize(bytes)).as("bytes").isEqualTo("460 bytes");
		assertThat(StringUtils.prettifySize(kbytes)).as("kbytes").isEqualTo("584.9kB");
		assertThat(StringUtils.prettifySize(mbytes)).as("mbytes").isEqualTo("429.8MB");
		assertThat(StringUtils.prettifySize(gbytes)).as("gbytes").isEqualTo("3.6GB");
	}
}
