package fr.ribesg.alix.network;

import org.apache.log4j.Logger;

/**
 * Just a class used for quick tools like pausing a Thread
 * without taking care of interruptions.
 *
 * @author Ribesg
 */
public class Tools {

	private final static Logger LOGGER = Logger.getLogger(Tools.class.getName());

	/**
	 * Pauses the calling Thread for the provided amount of
	 * time. Ignores any InterruptedException, but logs it.
	 *
	 * @param millis the time to pause
	 */
	public static void pause(final int millis) {
		try {
			Thread.sleep(millis);
		} catch (final InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
}
