package com.mongodb.memphis.mutator;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import org.bson.BsonDecimal128;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonNumber;
import org.bson.BsonValue;
import org.bson.types.Decimal128;

import com.google.gson.internal.LazilyParsedNumber;
import com.mongodb.memphis.annotations.Name;

@Name("numericMutator")
public class NumericMutator extends Mutator {

	Modifier modifier;
	Number value;

	@Override
	protected BsonValue mutate(BsonValue value, String[] attributes) {
		BsonNumber number = Optional.of(value)
				.filter(BsonValue::isNumber)
				.map(BsonValue::asNumber)
				.orElseThrow(() -> new IllegalArgumentException("BsonValue is not a numeric type"));

		// numeric modifiers passed in on attributes
		if (attributes.length == 4) {
			modifier = Modifier.valueOf(attributes[2].toUpperCase());
			this.value = new LazilyParsedNumber(attributes[3]);
		}

		Objects.requireNonNull(modifier, "modifier has not been set for " + getClass().getSimpleName());
		Objects.requireNonNull(value, "value has not been set for " + getClass().getSimpleName());

		switch (modifier) {
		case DEC:
			if (number.isDouble()) {
				return new BsonDouble(number.asDouble().getValue() - this.value.doubleValue());
			}
			else if (number.isInt32()) {
				return new BsonInt32(number.asInt32().getValue() - this.value.intValue());
			}
			else if (number.isInt64()) {
				return new BsonInt64(number.asInt64().getValue() - this.value.longValue());
			}
			else if (number.isDecimal128()) {
				return new BsonDecimal128(new Decimal128(number.asDecimal128().getValue().bigDecimalValue().subtract(BigDecimal.valueOf(this.value.doubleValue()))));
			}

		case INC:
			if (number.isDouble()) {
				return new BsonDouble(number.asDouble().getValue() + this.value.doubleValue());
			}
			else if (number.isInt32()) {
				return new BsonInt32(number.asInt32().getValue() + this.value.intValue());
			}
			else if (number.isInt64()) {
				return new BsonInt64(number.asInt64().getValue() + this.value.longValue());
			}
			else if (number.isDecimal128()) {
				return new BsonDecimal128(new Decimal128(number.asDecimal128().getValue().bigDecimalValue().add(BigDecimal.valueOf(this.value.doubleValue()))));
			}
		case DIV:
			if (number.isDouble()) {
				return new BsonDouble(number.asDouble().getValue() / this.value.doubleValue());
			}
			else if (number.isInt32()) {
				return new BsonInt32(number.asInt32().getValue() / this.value.intValue());
			}
			else if (number.isInt64()) {
				return new BsonInt64(number.asInt64().getValue() / this.value.longValue());
			}
			else if (number.isDecimal128()) {
				return new BsonDecimal128(new Decimal128(number.asDecimal128().getValue().bigDecimalValue().divide(BigDecimal.valueOf(this.value.doubleValue()))));
			}
		case MUL:
			if (number.isDouble()) {
				return new BsonDouble(number.asDouble().getValue() * this.value.doubleValue());
			}
			else if (number.isInt32()) {
				return new BsonInt32(number.asInt32().getValue() * this.value.intValue());
			}
			else if (number.isInt64()) {
				return new BsonInt64(number.asInt64().getValue() * this.value.longValue());
			}
			else if (number.isDecimal128()) {
				return new BsonDecimal128(new Decimal128(number.asDecimal128().getValue().bigDecimalValue().multiply(BigDecimal.valueOf(this.value.doubleValue()))));
			}
			default:
				return new BsonNull();
		}
	}

	enum Modifier {
		INC, DEC, MUL, DIV
	}

}
