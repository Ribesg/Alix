package fr.ribesg.alix.api.event.message;
import fr.ribesg.alix.api.Client;
import fr.ribesg.alix.api.event.Event;
import fr.ribesg.alix.api.message.Message;

/**
 * Called after the Client received a raw Message.
 *
 * @author Ribesg
 */
public class RawMessageReceivedEvent extends Event {

	private final Message rawMessage;

	public RawMessageReceivedEvent(final Client client, final Message rawMessage) {
		super(client);
		this.rawMessage = rawMessage;
	}

	/**
	 * Gets the raw Message this Client has just received.
	 *
	 * @return the raw Message this Client just received
	 */
	public Message getRawMessage() {
		return this.rawMessage;
	}
}
