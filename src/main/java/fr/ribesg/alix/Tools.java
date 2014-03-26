package fr.ribesg.alix;

import fr.ribesg.alix.api.Log;

/**
 * Just a class used for quick tools like pausing a Thread
 * without taking care of interruptions.
 *
 * @author Ribesg
 */
public class Tools {

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
			Log.error(e.getMessage(), e);
		}
	}
}
