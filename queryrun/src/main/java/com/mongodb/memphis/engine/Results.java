package com.mongodb.memphis.engine;

public class Results {
	long[] operationTimes;
	long size;
	long recordCount;

	public void initialise(int operationIterations) {
		operationTimes = new long[operationIterations];
		size = 0;
		recordCount = 0;
	}

	public long[] getOperationTimes() {
		return operationTimes;
	}

	public long getTotalSize() {
		return size;
	}

	public long getRecordCount() {
		return recordCount;
	}

	public void setOperationTime(int opIteration, long opTime) {
		operationTimes[opIteration] = opTime;
	}

	public void incSize(long size) {
		this.size += size;
	}

	public void incRecordCount(long recordCount) {
		this.recordCount += recordCount;
	}

	/**
	 * 			// load the query times into the stats engine
			LongStatistics stats = new LongStatistics();
			long totalSize = 0;
			long totalRecords = 0;
			for (Results result : results) {
				totalSize += result.size;
				totalRecords += result.recordCount;
				for (long time : result.insertTime) {
					stats.accept(time);
				}
			}

			logger.info("Total Records inserted: {}", totalRecords);
			logger.info("Insert rate: {}/s", Math.ceil(1000*totalRecords/totalTime));
			logger.info("Max insert time: {}", StringUtils.prettifyTime(stats.getMax()));
			logger.info("Min insert time: {}", StringUtils.prettifyTime(stats.getMin()));
			logger.info("Average insert time: {}", StringUtils.prettifyTime((long) stats.getAverage()));
			logger.info("Total insert time: {}", StringUtils.prettifyTime(totalTime));
			logger.info("SD of insert time: {}", stats.getStandardDeviation());
			logger.info("Total data transferred: {}", StringUtils.prettifySize(totalSize));
			logger.info("Transfer rate: {}", StringUtils.prettifyTransferRate(1000*totalSize/totalTime));
	 */

}
