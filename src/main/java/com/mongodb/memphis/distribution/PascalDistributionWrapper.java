package com.mongodb.memphis.distribution;

import org.apache.commons.math4.distribution.PascalDistribution;

import com.mongodb.memphis.annotations.Name;

@Name("pascal")
public class PascalDistributionWrapper extends IntegerDistributionWrapper {

    private int r;
    private double probability;


	@Override
	public void initialise() {
		dist = new PascalDistribution(r, probability);
	}


}
