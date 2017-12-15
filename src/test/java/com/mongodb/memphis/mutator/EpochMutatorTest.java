package com.mongodb.memphis.mutator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;

import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import com.mongodb.memphis.MockitoExtension;
import com.mongodb.memphis.engine.EngineDocument;

@ExtendWith(MockitoExtension.class)
public class EpochMutatorTest {

	@Mock
	EngineDocument engDoc;

	@Test
	public void chronoFieldTest() {
		EpochMutator mutator = new EpochMutator();
		mutator.input = "k";

		LocalDateTime dt = LocalDateTime.of(2017, 12, 15, 1, 30, 12);
		long epoch = dt.toInstant(ZoneOffset.UTC).toEpochMilli();

		when(engDoc.getDocument()).thenReturn(new BsonDocument(mutator.input, new BsonDateTime(epoch)));

		assertChronoField(ChronoField.DAY_OF_MONTH, mutator, dt);
		assertChronoField(ChronoField.YEAR, mutator, dt);
		assertChronoField(ChronoField.DAY_OF_YEAR, mutator, dt);
		assertChronoField(ChronoField.MONTH_OF_YEAR, mutator, dt);
		assertChronoField(ChronoField.SECOND_OF_DAY, mutator, dt);
		assertChronoField(ChronoField.HOUR_OF_DAY, mutator, dt);
		assertChronoField(ChronoField.MILLI_OF_SECOND, mutator, dt);
	}

	@Test
	public void lsbTest() {
		EpochMutator mutator = new EpochMutator();
		mutator.input = "k";

		long epoch = Long.MAX_VALUE;
		when(engDoc.getDocument()).thenReturn(new BsonDocument(mutator.input, new BsonDateTime(epoch)));

		assertThat(mutator.getValue(engDoc)).as("msb").isEqualTo(new BsonInt32(Integer.MAX_VALUE));
	}

	private void assertChronoField(ChronoField chrono, EpochMutator mutator, LocalDateTime dt) {
		mutator.chronoField = chrono;
		assertThat(mutator.getValue(engDoc)).as(chrono.toString()).isEqualTo(new BsonInt32(dt.get(mutator.chronoField)));
	}
}
