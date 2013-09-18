package fr.ribesg.alix.api.event;
import fr.ribesg.alix.api.event.connection.ChannelJoinedEvent;
import fr.ribesg.alix.api.event.connection.ServerJoinedEvent;
import fr.ribesg.alix.api.event.message.RawMessageReceivedEvent;

/**
 * Represents an Event Listener.
 * The user of this class should Override any Event Handler related to what
 * they want to achieve with Alix.
 *
 * @author Ribesg
 */
public abstract class EventListener {

	/**
	 * Event handler for ServerJoinedEvent.
	 *
	 * @param event the event to handle
	 */
	public void onServerJoined(final ServerJoinedEvent event) {}

	/**
	 * Event handler for ChannelJoinedEvent.
	 *
	 * @param event the event to handle
	 */
	public void onChannelJoined(final ChannelJoinedEvent event) {}

	/**
	 * Event handler for RawMessageReceivedEvent.
	 *
	 * @param event the event to handle
	 */
	public void onRawMessageReceived(final RawMessageReceivedEvent event) {}
}
