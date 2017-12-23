package com.mongodb.memphis.distribution;

import org.apache.commons.math4.distribution.PoissonDistribution;

import com.mongodb.memphis.annotations.Name;

@Name("poisson")
public class PoissonDistributionWrapper extends IntegerDistributionWrapper {

    private double probability;

	@Override
	public void initialise() {
		dist = new PoissonDistribution(probability);
	}

}
