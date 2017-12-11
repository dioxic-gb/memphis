package com.mongodb.memphis.util;

import java.util.LongSummaryStatistics;
import java.util.stream.Collector;

public class LongStatistics extends LongSummaryStatistics {

	private long sumOfSquare = 0l;
	private long sumOfSquareCompensation; // Low order bits of sum
	private long simpleSumOfSquare; // Used to compute right sum for
										// non-finite inputs

	@Override
	public void accept(long value) {
		super.accept(value);
		long squareValue = value * value;
		simpleSumOfSquare += squareValue;
		sumOfSquareWithCompensation(squareValue);
	}

	public LongStatistics combine(LongStatistics other) {
		super.combine(other);
		simpleSumOfSquare += other.simpleSumOfSquare;
		sumOfSquareWithCompensation(other.sumOfSquare);
		sumOfSquareWithCompensation(other.sumOfSquareCompensation);
		return this;
	}

	private void sumOfSquareWithCompensation(long value) {
		long tmp = value - sumOfSquareCompensation;
		long velvel = sumOfSquare + tmp; // Little wolf of rounding error
		sumOfSquareCompensation = (velvel - sumOfSquare) - tmp;
		sumOfSquare = velvel;
	}

	public long getSumOfSquare() {
		return sumOfSquare + sumOfSquareCompensation;
	}

	public final double getStandardDeviation() {
		long count = getCount();
		long sumOfSquare = getSumOfSquare();
		double average = getAverage();
		return count > 0 ? Math.sqrt((sumOfSquare - count * Math.pow(average, 2)) / (count - 1)) : 0.0d;
	}

	public static Collector<Long, ?, LongStatistics> collector() {
		return Collector.of(LongStatistics::new, LongStatistics::accept, LongStatistics::combine);
	}

}
