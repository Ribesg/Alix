package fr.ribesg.alix.internal.thread;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class represents a Java Condition that has its own Lock.
 *
 * @author Ribesg
 */
public class SimpleCondition implements Condition {

	private final java.util.concurrent.locks.Lock lock      = new ReentrantLock();
	private final Condition                       condition = lock.newCondition();

	@Override
	public void await() throws InterruptedException {
		this.lock.lock();
		try {
			this.condition.await();
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public void awaitUninterruptibly() {
		this.lock.lock();
		try {
			this.condition.awaitUninterruptibly();
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public long awaitNanos(final long nanosTimeout) throws InterruptedException {
		this.lock.lock();
		try {
			return this.condition.awaitNanos(nanosTimeout);
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public boolean await(final long time, final TimeUnit unit) throws InterruptedException {
		this.lock.lock();
		try {
			return this.condition.await(time, unit);
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public boolean awaitUntil(final Date deadline) throws InterruptedException {
		this.lock.lock();
		try {
			return this.condition.awaitUntil(deadline);
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public void signal() {
		this.lock.lock();
		try {
			this.condition.signal();
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public void signalAll() {
		this.lock.lock();
		try {
			this.condition.signalAll();
		} finally {
			this.lock.unlock();
		}
	}
}
