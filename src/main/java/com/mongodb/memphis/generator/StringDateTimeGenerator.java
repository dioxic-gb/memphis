package com.mongodb.memphis.generator;

import org.bson.BsonString;
import org.bson.BsonValue;

import com.mongodb.memphis.annotations.Name;

@Name("stringdatetime")
public class StringDateTimeGenerator extends Generator<String> {

    @Override
    protected String generateValue() {
        StringBuilder sb = new StringBuilder(10);

        sb.append(nextInt(0,33)+2017);
        sb.append('-');
        int month = nextInt(1,13);
        if (month < 10) {
            sb.append(0);
        }
        sb.append(month);
        sb.append('-');
        int day = nextInt(1,29);
        if (day < 10) {
            sb.append(0);
        }
        sb.append(day);
        return sb.toString();
    }

	@Override
	protected BsonValue toBson(String value) {
		return new BsonString(value);
	}

}
