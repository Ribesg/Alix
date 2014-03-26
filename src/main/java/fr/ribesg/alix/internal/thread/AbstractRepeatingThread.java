package fr.ribesg.alix.internal.thread;
/**
 * Represents a Thread that will usually do some work every X milliseconds.
 * <p>
 * Other threads can ask this Thread to stop with the {@link #askStop()}
 * method, then call {@link #join()}.
 */
public abstract class AbstractRepeatingThread extends Thread {

	/**
	 * The delay between each call to {@link #work()}
	 */
	protected final int loopTime;

	/**
	 * This is the main AbstractRepeatingThread constructor.
	 *
	 * @param name     the name of this Thread
	 * @param loopTime the delay between each call to {@link #work()}
	 */
	public AbstractRepeatingThread(final String name, final int loopTime) {
		super(name);
		this.loopTime = loopTime;
	}

	/**
	 * Asks this Thread to stop.
	 */
	public void askStop() {
		this.interrupt();
	}

	@Override
	public final void run() {
		try {
			while (!this.isInterrupted()) {
				this.work();
				Thread.sleep(this.loopTime);
			}
		} catch (final InterruptedException e) {
			this.interrupt();
		}
	}

	/**
	 * This method is ran every {@link #loopTime} until this Thread is asked
	 * to stop or dies.
	 * <p>
	 * Note: This method should never block for a too long time, to be able
	 * to close the Thread properly in no more than some seconds.
	 *
	 * @throws java.lang.InterruptedException if the Thread is interrupted
	 */
	protected abstract void work() throws InterruptedException;
}
