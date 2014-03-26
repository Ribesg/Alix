package fr.ribesg.alix.internal.callback;
import fr.ribesg.alix.api.Client;
import fr.ribesg.alix.api.Log;
import fr.ribesg.alix.api.callback.Callback;
import fr.ribesg.alix.api.callback.CallbackPriority;
import fr.ribesg.alix.api.message.IrcPacket;
import fr.ribesg.alix.internal.thread.AbstractRepeatingThread;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * This class handles Callbacks.
 */
public class CallbackHandler {

	/**
	 * Comparator used to create SortedSets of Callbacks sorted by closest
	 * timeout first.
	 */
	private static final Comparator<Callback> callbackTimeoutComparator = (a, b) -> Long.compare(a.getTimeoutDate(), b.getTimeoutDate());

	/**
	 * The registered Callbacks, per priority, sorted by closest timeout first.
	 */
	private final Map<CallbackPriority, SortedSet<Callback>> prioritizedCallbacks;

	private final CallbacksCleanerThread cleanerThread;

	/**
	 * Main CallbackHandler constructor.
	 */
	public CallbackHandler() {
		final Map<CallbackPriority, SortedSet<Callback>> map = new EnumMap<>(CallbackPriority.class);
		map.put(CallbackPriority.HIGHEST, new ConcurrentSkipListSet<>(callbackTimeoutComparator));
		map.put(CallbackPriority.HIGH, new ConcurrentSkipListSet<>(callbackTimeoutComparator));
		map.put(CallbackPriority.LOW, new ConcurrentSkipListSet<>(callbackTimeoutComparator));
		map.put(CallbackPriority.LOWEST, new ConcurrentSkipListSet<>(callbackTimeoutComparator));
		this.prioritizedCallbacks = Collections.unmodifiableMap(map);
		this.cleanerThread = new CallbacksCleanerThread(this.prioritizedCallbacks);

		this.cleanerThread.start();
	}

	/**
	 * Kill this CallbackHandler
	 */
	public void kill() {
		this.cleanerThread.askStop();
		try {
			this.cleanerThread.join();
		} catch (final InterruptedException e) {
			Log.error(e);
		}
	}

	/**
	 * Register a new Callback.
	 *
	 * @param callback the callback to register
	 */
	public void registerCallback(final Callback callback) {
		this.prioritizedCallbacks.get(callback.getPriority()).add(callback);
	}

	/**
	 * See if a Callback handles an incoming IRC Packet.
	 *
	 * @param packet the incoming IRC Packet
	 */
	public void handle(final CallbackPriority priority, final IrcPacket packet) {
		final Set<Callback> callbacks = this.prioritizedCallbacks.get(priority);
		final String code = packet.getRawCommandString().toUpperCase();
		final long now = System.currentTimeMillis();
		final Iterator<Callback> it = callbacks.iterator();
		while (it.hasNext()) {
			final Callback callback = it.next();
			if (callback.getTimeoutDate() < now) {
				Client.getThreadPool().submit(callback::onTimeout);
				it.remove();
			} else if (callback.listensTo(code)) {
				if (callback.onIrcPacket(packet)) {
					Log.debug("DEBUG: Packet handled by a Callback!");
					it.remove();
				}
			}
		}
	}

	/**
	 * The purpose of this Thread is to take a SortedSet of
	 * Callbacks and to check that every Callback is still valid,
	 * i.e. every Callback didn't timeout.
	 */
	private class CallbacksCleanerThread extends AbstractRepeatingThread {

		/**
		 * The callbacks
		 */
		private final Map<CallbackPriority, SortedSet<Callback>> prioritizedCallbacks;

		/**
		 * Main CallbacksCleanerThread constructor.
		 *
		 * @param prioritizedCallbacks the prioritizedCallbacks to monitor
		 */
		public CallbacksCleanerThread(final Map<CallbackPriority, SortedSet<Callback>> prioritizedCallbacks) {
			super("Cb-Cleaner", 1_000);
			this.prioritizedCallbacks = prioritizedCallbacks;
		}

		/**
		 * Will check the prioritizedCallbacks Set every second
		 */
		@Override
		public void work() {
			for (final CallbackPriority priority : this.prioritizedCallbacks.keySet()) {
				final Set<Callback> callbacks = this.prioritizedCallbacks.get(priority);
				if (!this.prioritizedCallbacks.isEmpty()) {
					final long now = System.currentTimeMillis();
					final Iterator<Callback> it = callbacks.iterator();
					boolean removedCallback;
					do {
						removedCallback = false;
						final Callback callback = it.next();
						if (callback.getTimeoutDate() < now) {
							callback.onTimeout();
							it.remove();
							removedCallback = true;
						}
					} while (it.hasNext() && removedCallback);
				}
			}
		}
	}

}
