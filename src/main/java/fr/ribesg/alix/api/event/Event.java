package fr.ribesg.alix.api.event;
import fr.ribesg.alix.api.Client;

/**
 * Represents an event.
 * <p/>
 * Could be a Message received, sent, anything that can happen.
 *
 * @author Ribesg
 */
public abstract class Event {

	private final Client client;
	private final String name;

	protected Event(final Client client) {
		this.client = client;
		this.name = this.getClass().getSimpleName();
	}

	/**
	 * The client involved in this event.
	 *
	 * @return the client involved in this event
	 */
	public Client getClient() {
		return this.client;
	}

	/**
	 * The name of this event, by default the Event Class name.
	 *
	 * @return the name of this event
	 */
	public String getEventName() {
		return this.name;
	}
}
