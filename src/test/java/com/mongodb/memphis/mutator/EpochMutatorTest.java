package com.mongodb.memphis.mutator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.stream.Stream;

import org.bson.BsonDateTime;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;

import com.mongodb.memphis.MockitoExtension;
import com.mongodb.memphis.engine.DocumentPool.Batch;
import com.mongodb.memphis.engine.EngineDocument;

@ExtendWith(MockitoExtension.class)
public class EpochMutatorTest {

	@Mock
	EngineDocument engDoc;

	@Mock
	Batch batch;

	@ParameterizedTest
	@MethodSource("supportedChronoFields")
	public void chronoFieldTest(ChronoField chrono) {
		EpochMutator mutator = new EpochMutator();
		String input = "k";

		LocalDateTime dt = LocalDateTime.of(2017, 12, 15, 1, 30, 12);
		long epoch = dt.toInstant(ZoneOffset.UTC).toEpochMilli();

		when(engDoc.getFieldValue(input)).thenReturn(new BsonDateTime(epoch));

		mutator.chronoField = chrono;
		BsonValue expected = chrono.range().getMaximum() > Integer.MAX_VALUE ? new BsonInt64(dt.getLong(chrono)) : new BsonInt32(dt.get(chrono));

		assertThat(mutator.getScopedValue(engDoc, batch, new String[] {null, input})).as(chrono.toString()).isEqualTo(expected);
	}

	@Test
	public void lsbTest() {
		EpochMutator mutator = new EpochMutator();
		String input = "k";

		long epoch = Long.MAX_VALUE;
		when(engDoc.getFieldValue(input)).thenReturn(new BsonDateTime(epoch));

		assertThat(mutator.getScopedValue(engDoc, batch, new String[] {null, input})).as("msb").isEqualTo(new BsonInt32(Integer.MAX_VALUE));
	}

	static Stream<ChronoField> supportedChronoFields() {
		return Arrays.stream(ChronoField.values())
				.filter(f -> f.isDateBased() || f.isTimeBased());
	}

}
