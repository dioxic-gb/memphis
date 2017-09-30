package com.mongodb.mepee.datagen;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class RngWrapper {

	    private final Random random = new Random();

	    //private volatile Set<String> tradeIdCache = new HashSet<>(100000);

	    private volatile BitSet tradeIdCache = new BitSet();

	    private LinkedHashMap<Integer,String> tradeCache;
	    private static final int MAX_ENTRIES = 200000;

	    private final int MAGIC_NUMBER = 37;

	    private final long epochNow = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

	    // 2010-01-01
	    private final long epochPast = 1262304000000l;

	    private final AtomicInteger counter;

	   RngWrapper()
	   {
		   counter = new AtomicInteger(0);
		   tradeCache = new LinkedHashMap<Integer,String>(MAX_ENTRIES+1, .75F, true) {

			private static final long serialVersionUID = 1L;

				// This method is called just after a new entry has been added
	            @Override
				public boolean removeEldestEntry(Map.Entry<Integer,String> eldest) {
	                return size() > MAX_ENTRIES;
	            }
	        };
	   }

	    public int generateInt(int bound) {
	        return random.nextInt(bound);
	    }

	    public Double generatePrice() {
	        return random.nextDouble()*1000000.0;
	    }


	    public String generateDate() {
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
	        return sb.toString();
	    }


	    public String generateId(String prefix, int max) {
	        int length = String.valueOf(max).length();
	        StringBuilder sb = new StringBuilder (prefix.length() + length);
	        sb.append(prefix);
	        int value = random.nextInt(max)+1;
	        int idSize = String.valueOf(value).length();
	        int prepad = length-idSize;
	        for ( int i =0;i<prepad;i++) {
	            sb.append('0');
	        }
	        sb.append(value);
	        return sb.toString();
	    }

	    // - rewrite much cleaner - TODO  - make this one random number
	    // Can be faster with a single random number
	    public String generateDateTime() {

	    	// epoch 1262304000000 = 2010-01-01

	          int day = random.nextInt(28)+1;
	          int year = random.nextInt(5)+2015;
	          int month = random.nextInt(12)+1;
	          int hour = random.nextInt(24);
	          int minute = random.nextInt(59)+1;
	          int second = random.nextInt(59)+1;

//	          return LocalDateTime.of(year, month, day, hour, minute, second);


	        return String.format("%d-%02d-%02dT%02d:%02d:%02dZ",
	                year,month,day,
	                hour, minute, second);
	   }

	    public long generateEpoch() {
	    	int bound = (int)(epochNow - epochPast);
	    	return random.nextInt(bound) + epochPast;
	    }

	    String generateTradeId() {
	        String tradeId = null;

	        //Return an existing one one time in 37
	        if (random.nextInt(1000000) % MAGIC_NUMBER == 0) {
	            tradeId = tradeCache.get(random.nextInt(MAX_ENTRIES));
	        }

	        if (tradeId == null){

	            int value = random.nextInt(999999999)+1;
	            while (tradeIdCache.get(value)) {
	            	 value = random.nextInt(999999999)+1;
	            }
	            tradeIdCache.set(value);
	            tradeId=String.format("%09d", value);

	            int mapKey = counter.getAndIncrement();
	            if (mapKey >= MAX_ENTRIES) {
	                counter.set(0);
	                mapKey = 0;
	            }
	            tradeCache.put(mapKey,tradeId);
	        }
	        return tradeId;
	    }



}
