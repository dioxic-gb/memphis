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

}
