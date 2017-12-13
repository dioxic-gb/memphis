package com.mongodb.memphis.engine;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.junit.Test;

import com.mongodb.memphis.config.Template;

public class DocumentPoolTest {

	@Test
	public void calculateBigWeightingArrayTest() {
		List<Template> templates = new ArrayList<>();
		templates.add(generateTemplate("1", 10));
		templates.add(generateTemplate("2", 20));
		templates.add(generateTemplate("3", 75));

		assertWeightingArray(templates, 22);
	}

	public void assertWeightingArray(List<Template> templates, int batchSize) {
		int totalWeights = templates.stream().mapToInt(Template::getWeighting).sum();
		DocumentPool docPool = new DocumentPool(templates, batchSize);

		List<Template> weightedTemplates = docPool.getWeightedList();

		assertThat(weightedTemplates).as("weighted template array").containsOnlyElementsOf(templates);

		Map<Template, Long> counted = weightedTemplates.stream()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		for (Template template : templates) {
			String key = template.getReferenceDocument().get("k").asString().getValue();
			assertThat(counted.get(template)).as("template " + key + " count").isEqualTo(weightedTemplates.size() * template.getWeighting() / totalWeights);
		}

		System.out.println("poolSize: " + weightedTemplates.size());
		//weightedTemplates.stream().map(Template::getTemplate).forEach(System.out::println);
	}

	private Template generateTemplate(String value, int weighting) {
		Template template = new Template();

		template.setTemplate(new BsonDocument("k", new BsonString(value)));
		template.setWeighting(weighting);

		return template;
	}
}
