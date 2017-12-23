package com.mongodb.memphis.distribution;

import org.apache.commons.math4.distribution.IntegerDistribution;
import org.apache.commons.math4.exception.NumberIsTooLargeException;
import org.apache.commons.math4.exception.OutOfRangeException;
import org.apache.commons.rng.UniformRandomProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class IntegerDistributionWrapper implements IntegerDistribution {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected transient IntegerDistribution dist;
	protected transient int lowerBound = 0;
	protected transient int upperBound = Integer.MAX_VALUE;

	public void setLowerBound(int lowerBound) {
		this.lowerBound = lowerBound;
	}

	public void setUpperBound(int upperBound) {
		this.upperBound = upperBound;
	}

	public abstract void initialise();

	@Override
	public double logProbability(int x) {
		return dist.logProbability(x);
	}

	@Override
	public double probability(int x) {
		return dist.probability(x);
	}

	@Override
	public double probability(int x0, int x1) throws NumberIsTooLargeException {
		return dist.probability(x0, x1);
	}

	@Override
	public double cumulativeProbability(int x) {
		return dist.cumulativeProbability(x);
	}

	@Override
	public int inverseCumulativeProbability(double p) throws OutOfRangeException {
		return dist.inverseCumulativeProbability(p);
	}

	@Override
	public double getNumericalMean() {
		return dist.getNumericalMean();
	}

	@Override
	public double getNumericalVariance() {
		return dist.getNumericalVariance();
	}

	@Override
	public int getSupportLowerBound() {
		return dist.getSupportLowerBound();
	}

	@Override
	public int getSupportUpperBound() {
		return dist.getSupportUpperBound();
	}

	@Override
	public boolean isSupportConnected() {
		return dist.isSupportConnected();
	}

	@Override
	public Sampler createSampler(UniformRandomProvider rng) {
		return new SamplerWrapper(dist.createSampler(rng));
	}

	class SamplerWrapper implements IntegerDistribution.Sampler {

		IntegerDistribution.Sampler sampler;

		public SamplerWrapper(Sampler sampler) {
			this.sampler = sampler;
		}

		@Override
		public int sample() {
			int val = 0;
			int count = 0;

			while (count < 100) {
				val = sampler.sample();
				val += lowerBound;
				if (val < upperBound) {
					break;
				}
				count++;
			}

			if (count == 100) {
				logger.warn("could not bound the output of the distribution function to [{},{}]", lowerBound, upperBound);
				val = lowerBound;
			}
			return val;
		}
	}
}
