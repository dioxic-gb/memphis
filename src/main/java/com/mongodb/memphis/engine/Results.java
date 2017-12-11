package com.mongodb.memphis.engine;

import java.util.Arrays;
import java.util.LongSummaryStatistics;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.annotations.ThreadSafe;
import com.mongodb.memphis.util.StringUtils;

@ThreadSafe
public class Results {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private long[][] operationTimes;
	private AtomicLong bytesRead;
	private AtomicLong bytesWritten;
	private AtomicLong docsRead;
	private AtomicLong docsWritten;

	public Results(int threads, int operationIterations) {
		operationTimes = new long[threads][operationIterations];
		bytesRead = new AtomicLong(0);
		bytesWritten = new AtomicLong(0);
		docsRead = new AtomicLong(0);
		docsWritten = new AtomicLong(0);
	}

	public long[][] getOperationTimes() {
		return operationTimes;
	}

	public LongSummaryStatistics reduceStatistics() {
		return Arrays.stream(operationTimes).flatMapToLong(Arrays::stream).summaryStatistics();
	}

	public void setOperationTime(int thread, int opIteration, long opTime) {
		operationTimes[thread][opIteration] = opTime;
	}

	public long getBytesRead() {
		return bytesRead.get();
	}

	public void bytesRead(long bytesRead) {
		this.bytesRead.addAndGet(bytesRead);
	}

	public long getBytesWritten() {
		return bytesWritten.get();
	}

	public void bytesWritten(long bytesWritten) {
		this.bytesWritten.addAndGet(bytesWritten);
	}

	public long getDocsRead() {
		return docsRead.get();
	}

	public void docsRead(long docsRead) {
		this.docsRead.addAndGet(docsRead);
	}

	public long getDocsWritten() {
		return docsWritten.get();
	}

	public void docsWritten(long docsWritten) {
		this.docsWritten.addAndGet(docsWritten);
	}

	public void mergeResults(Results results) {
		bytesRead(results.getBytesRead());
		bytesWritten(results.getBytesWritten());
		docsRead(results.getDocsRead());
		docsWritten(results.getDocsWritten());
	}

	public void printResults() {
		LongSummaryStatistics stats = reduceStatistics();

		long totalTime = stats.getSum() / operationTimes.length;

		if (totalTime > 0) {
			logger.info("Total time: {}", StringUtils.prettifyTime(totalTime));
			logger.info("Insert rate: [{}, {}]", StringUtils.prettifyRate(" docs", docsWritten.get() / totalTime), StringUtils.prettifyTransferRate(bytesWritten.get() / totalTime));
			logger.info("Read rate: [{}, {}]", StringUtils.prettifyRate(" docs", docsRead.get() / totalTime), StringUtils.prettifyTransferRate(bytesRead.get() / totalTime));
			logger.info("Operation time: [min: {}, max: {}, avg: {}]", StringUtils.prettifyTime(stats.getMin()), StringUtils.prettifyTime(stats.getMax()), StringUtils.prettifyTime((long) stats.getAverage()));
			// logger.info("SD of insert time: {}",
			// stats.getStandardDeviation());
			logger.info("Total written: [{} documents, {}]", docsWritten, StringUtils.prettifySize(bytesWritten.get()));
			logger.info("Total read: [{} documents, {}]", docsRead, StringUtils.prettifySize(bytesRead.get()));
		}
	}

}
