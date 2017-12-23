package com.mongodb.memphis.distribution;

import org.apache.commons.math4.distribution.HypergeometricDistribution;

import com.mongodb.memphis.annotations.Name;

@Name("hypergeometric")
public class HypergeometricDistributionWrapper extends IntegerDistributionWrapper {

    private int numberOfSuccesses;
    private int populationSize;
    private int sampleSize;

	@Override
	public void initialise() {
		dist = new HypergeometricDistribution(populationSize, numberOfSuccesses, sampleSize);
	}

}
