package main;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CachedFuture<T> {

	private T result;
	private boolean hasResult;
	private Future<T> future;

	public CachedFuture(Future<T> future) {
		this.future = future;
		result = null;
		hasResult = false;
	}

	public T get() {
		if (!hasResult) {
			try {
				while (!hasResult) {
					try {
						result = future.get();
						hasResult = true;
					} catch (InterruptedException e) {
						System.out.println("DEBUG: Thread interrupted. Trying again...");
					}
				}
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException("ExecutionException handler not yet written in get of CachedFuture.", e);
			}
		}
		return result;
	}

	public boolean isDone() {
		if (hasResult) {
			return true;
		}

		if (future.isDone()) {
			try {
				result = future.get();
				hasResult = true;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException("InterruptedException handler not yet written in isDone of CachedFuture.", e);
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException("ExecutionException handler not yet written in isDone of CachedFuture.", e);
			}
			return true;
		}

		return false;
	}
}
