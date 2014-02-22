package fr.ribesg.alix.api;
public class Tools {

	public static void pause(final int millis) {
		try {
			Thread.sleep(millis);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}
}
