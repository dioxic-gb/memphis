package com.mongodb.memphis.distribution;

import org.apache.commons.math4.distribution.ZipfDistribution;

import com.mongodb.memphis.annotations.Name;

@Name("zipf")
public class ZipfDistributionWrapper extends IntegerDistributionWrapper {

    private int numberOfElements;
    private double exponent;

	@Override
	public void initialise() {
		dist = new ZipfDistribution(numberOfElements, exponent);
	}


}
