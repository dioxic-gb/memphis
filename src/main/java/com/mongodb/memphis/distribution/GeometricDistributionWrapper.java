package com.mongodb.memphis.distribution;

import org.apache.commons.math4.distribution.GeometricDistribution;

import com.mongodb.memphis.annotations.Name;

@Name("geometric")
public class GeometricDistributionWrapper extends IntegerDistributionWrapper {

	private double probability;

	@Override
	public void initialise() {
		dist = new GeometricDistribution(probability);
	}

}
