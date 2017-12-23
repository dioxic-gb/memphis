package com.mongodb.memphis.distribution;

import org.apache.commons.math4.distribution.BinomialDistribution;

import com.mongodb.memphis.annotations.Name;

@Name("binomial")
public class BinomialDistributionWrapper extends IntegerDistributionWrapper {

    private int trials;
    private double probability;


	@Override
	public void initialise() {
		dist = new BinomialDistribution(trials, probability);
	}


}
