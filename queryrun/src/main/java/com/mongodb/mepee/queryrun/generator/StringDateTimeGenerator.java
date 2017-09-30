package com.mongodb.mepee.queryrun.generator;

import java.util.Random;

import org.bson.BsonString;
import org.bson.BsonValue;

import com.mongodb.mepee.queryrun.placeholder.Placeholder;

public class StringDateTimeGenerator extends AbstractGenerator implements Generator, Placeholder {

	private final Random random = new Random();

    @Override
	public BsonValue getValue() {
        StringBuilder sb = new StringBuilder(10);

        sb.append(random.nextInt(33)+2017);
        sb.append('-');
        int month = random.nextInt(12)+1;
        if (month < 10) {
            sb.append(0);
        }
        sb.append(month);
        sb.append('-');
        int day = random.nextInt(28)+1;
        if (day < 10) {
            sb.append(0);
        }
        sb.append(day);
        return new BsonString(sb.toString());
    }

}
