package com.mongodb.memphis.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.Validate;
import org.bson.BsonDocument;

import com.mongodb.client.MongoCollection;
import com.mongodb.memphis.config.Template;
import com.mongodb.memphis.engine.DocumentPool;
import com.mongodb.memphis.engine.Results;

public abstract class DataOperation extends Operation {

	protected int threads = 1;
	protected List<Template> templates;

	private transient Results operationResults;

	@Override
	public List<Template> getChildren() {
		return templates;
	}

	public int getThreads() {
		return threads;
	}

	public Results getResults() {
		return operationResults;
	}

	@Override
	protected void initialise() {
		super.initialise();
		operationResults = new Results(getThreads(), getIterations());
	}

	@Override
	protected void validate() {
		super.validate();
		Validate.notNull(templates, "templates not set for %s", this.getClass().getSimpleName());
	}

	protected abstract int getIterations();

	protected abstract DocumentPool createDocumentPool();

	protected abstract void execute(MongoCollection<BsonDocument> collection, DocumentPool documentPool, Results results);

	@Override
	protected void executeInternal() {
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		List<Worker> tasks = new ArrayList<>(threads);

		for (int i = 0; i < threads; i++) {
			tasks.add(new Worker(i));
		}

		try {
			executor.invokeAll(tasks);
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		executor.shutdown();

		operationResults.printResults();
	}

	class Worker implements Callable<Void> {
		int threadNum;

		public Worker(int threadNumber) {
			this.threadNum = threadNumber;
		}

		@Override
		public Void call() throws Exception {
			logger.debug("Thread {} starting", threadNum);
			try {
				DocumentPool docPool = createDocumentPool();
				MongoCollection<BsonDocument> collection = getMongoCollection();

				docPool.initialise();

				for (int counter = 0; counter < getIterations(); counter++) {
					logger.trace("Thread {} running operation iteration {}", threadNum, counter);

					docPool.nextBatch(counter);

					long startTime = System.currentTimeMillis();
					execute(collection, docPool, operationResults);
					long totalTime = System.currentTimeMillis() - startTime;

					operationResults.setOperationTime(threadNum, counter, totalTime);
				}
			}
			catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw e;
			}

			logger.debug("Thread {} complete processed {} operations", threadNum, getIterations());
			return null;
		}
	}

}
