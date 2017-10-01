package com.mongodb.memphis.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadResults {

	private Logger logger = LoggerFactory.getLogger(getClass());

	long[] operationTimes;
	long bytesRead;
	long bytesWritten;
	long docsRead;
	long docsWritten;

	public void initialise(int operationIterations) {
		operationTimes = new long[operationIterations];
		bytesRead = 0;
		bytesWritten = 0;
		docsRead = 0;
		docsWritten = 0;
	}

	public long[] getOperationTimes() {
		return operationTimes;
	}

	public void setOperationTime(int opIteration, long opTime) {
		operationTimes[opIteration] = opTime;
	}

	public long getBytesRead() {
		return bytesRead;
	}

	public void bytesRead(long bytesRead) {
		this.bytesRead = +bytesRead;
	}

	public long getBytesWritten() {
		return bytesWritten;
	}

	public void bytesWritten(long bytesWritten) {
		this.bytesWritten = +bytesWritten;
	}

	public long getDocsRead() {
		return docsRead;
	}

	public void docsRead(long docsRead) {
		this.docsRead = +docsRead;
	}

	public long getDocsWritten() {
		return docsWritten;
	}

	public void docsWritten(long docsWritten) {
		this.docsWritten = +docsWritten;
	}

	public void mergeResults(Results results) {
		bytesRead(results.getBytesRead());
		bytesWritten(results.getBytesWritten());
		docsRead(results.getDocsRead());
		docsWritten(results.getDocsWritten());
	}

}
