package fr.ribesg.alix.internal.callback;
import fr.ribesg.alix.Tools;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.callback.Callback;
import fr.ribesg.alix.api.message.IrcPacket;
import org.apache.log4j.Logger;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class handles Callbacks.
 */
public class CallbackHandler {

	private static final Logger LOGGER = Logger.getLogger(CallbackHandler.class.getName());

	/**
	 * Comparator used to create SortedSets of Callbacks sorted by closest
	 * timeout first.
	 */
	private static final Comparator<Callback> callbackTimeoutComparator = new Comparator<Callback>() {

		@Override
		public int compare(final Callback a, final Callback b) {
			return Long.compare(a.getTimeoutDate(), b.getTimeoutDate());
		}
	};

	/**
	 * The registered Callbacks, sorted by closest timeout first.
	 */
	private final SortedSet<Callback> callbacks;

	private final CallbacksCleanerThread cleanerThread;

	/**
	 * Main CallbackHandler constructor.
	 */
	public CallbackHandler() {
		this.callbacks = new TreeSet<>(callbackTimeoutComparator);
		this.cleanerThread = new CallbacksCleanerThread(this.callbacks);

		this.cleanerThread.start();
	}

	/**
	 * Kill this CallbackHandler
	 */
	public void destroy() {
		this.cleanerThread.stopAsked = true;
		try {
			this.cleanerThread.join();
		} catch (final InterruptedException e) {
			LOGGER.error(e);
		}
	}

	/**
	 * Register a new Callback.
	 *
	 * @param callback the callback to register
	 */
	public void registerCallback(final Callback callback) {
		this.callbacks.add(callback);
	}

	/**
	 * See if a Callback handles an incoming IRC Packet.
	 *
	 * @param packet the incoming IRC Packet
	 *
	 * @return true if the IRC packet was handled by a Callback, false
	 * otherwise
	 */
	public boolean handle(final Server server, final IrcPacket packet) {
		boolean result = false;
		final String code = packet.getRawCommandString().toUpperCase();
		synchronized (this.callbacks) {
			final long now = System.currentTimeMillis();
			final Iterator<Callback> it = this.callbacks.iterator();
			while (it.hasNext() && !result) {
				final Callback callback = it.next();
				if (callback.getTimeoutDate() < now) {
					callback.timeout();
					it.remove();
				} else if (callback.isServer(server) && callback.listensTo(code)) {
					if (callback.onIrcPacket(packet)) {
						result = true;
					}
				}
			}
		}
		return result;
	}

	/**
	 * The purpose of this Thread is to take a SortedSet of
	 * Callbacks and to check that every Callback is still valid,
	 * i.e. every Callback didn't timeout.
	 */
	private class CallbacksCleanerThread extends Thread {

		/**
		 * The callbacks
		 */
		private final SortedSet<Callback> callbacks;

		/**
		 * If this Thread should be stopped
		 */
		private boolean stopAsked = false;

		/**
		 * Main CallbacksCleanerThread constructor.
		 *
		 * @param callbacks the callbacks to monitor
		 */
		public CallbacksCleanerThread(final SortedSet<Callback> callbacks) {
			this.callbacks = callbacks;
		}

		/**
		 * Will check the callbacks Set every second
		 */
		@Override
		public void run() {
			while (!this.stopAsked) {
				synchronized (this.callbacks) {
					if (!this.callbacks.isEmpty()) {
						final long now = System.currentTimeMillis();
						final Iterator<Callback> it = this.callbacks.iterator();
						boolean removedCallback;
						do {
							removedCallback = false;
							final Callback callback = it.next();
							if (callback.getTimeoutDate() < now) {
								callback.timeout();
								it.remove();
								removedCallback = true;
							}
						} while (it.hasNext() && removedCallback);
					}
				}
			}
			Tools.pause(1_000);
		}
	}

}
