package fr.ribesg.alix.internal.thread;
import fr.ribesg.alix.api.Log;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/** @author Ribesg */
public class CallbackLock {

	private final java.util.concurrent.locks.Lock lock      = new ReentrantLock();
	private final Condition                       condition = lock.newCondition();

	public void waitCallback() {
		lock.lock();
		try {
			condition.await();
		} catch (final InterruptedException e) {
			Log.error(e);
		} finally {
			lock.unlock();
		}
	}

	public void done() {
		lock.lock();
		try {
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}
}
