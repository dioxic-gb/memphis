package com.mongodb.memphis.distribution;

import java.util.Arrays;

import org.apache.commons.math4.distribution.EnumeratedIntegerDistribution;

import com.mongodb.memphis.annotations.Name;

@Name("enumerated")
public class EnumeratedDistributionWrapper extends IntegerDistributionWrapper {

    private double[] weights;
    private double minWeight;

	@Override
	public void initialise() {
		if (weights == null) {
			throw new IllegalStateException("weights not set for enumerated distribution type");
		}

		double[] originalProb = weights;
		int[] singletons = new int[upperBound-lowerBound];
		double[] probabilities = new double[upperBound-lowerBound];

		Arrays.fill(probabilities, minWeight);
		for (int i=0; i< originalProb.length; i++) {
			probabilities[i] = originalProb[i];
		}

		for (int i=0; i< singletons.length; i++) {
			singletons[i] = i;
		}
		dist = new EnumeratedIntegerDistribution(singletons, probabilities);
	}


}
