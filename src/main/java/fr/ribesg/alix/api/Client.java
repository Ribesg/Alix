package fr.ribesg.alix.api;
import fr.ribesg.alix.api.message.Message;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents an IRC Client that can connect to IRC servers,
 * join channels on those various IRC servers, etc.
 *
 * @author Ribesg
 */
public abstract class Client {

	/**
	 * Name of this Client, used as Nick
	 */
	private final String name;

	/**
	 * Servers this Client will join or has joined
	 */
	private final Set<Server> servers;

	/**
	 * Constructs an IRC Client, call the {@link #load()} method then the
	 * {@link #connectToServers()} method.
	 *
	 * @param name the name of the Client
	 */
	protected Client(final String name) {
		this.name = name;
		this.servers = new HashSet<>();

		load();

		connectToServers();
	}

	/**
	 * Gets the name of this Client.
	 *
	 * @return the name of this Client
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * Gets the servers this Client will connect to
	 * or is connected to.
	 *
	 * @return the servers this Client will connect to or is connected to
	 */
	public final Set<Server> getServers() {
		return this.servers;
	}

	/**
	 * This method is called by the Constructor.
	 * That's typically where you should load your
	 * config files or ask for user input to populate
	 * the {@link #servers} Set.
	 * <p/>
	 * After calling this method, the Client will try to
	 * connect to all servers ({@link #connectToServers()})
	 *
	 * @see fr.ribesg.alix.TestClient for example
	 */
	protected abstract void load();

	/**
	 * Initialize connection with all configured servers.
	 * Once sockets are opened and everything's fine, the Client
	 * will automagically try to join known channels.
	 *
	 * @see Server#joinChannels()
	 */
	private void connectToServers() {
		for (final Server server : this.servers) {
			server.connect();
		}
	}

	// ************************************************************************* //
	// ** Below this comment are methods supposed to be overridden by the API ** //
	// ** user for him to be able to do stuff. That's kind of Events, yes!    ** //
	// ************************************************************************* //

	/**
	 * Executed once the Client successfully connects to a Server.
	 * To be more precise, this is triggered once the Client receive
	 * the welcome message
	 * ({@link fr.ribesg.alix.api.enums.Reply#RPL_WELCOME}) from the Server.
	 * At this point the Client asked to joined defined Channels, but has not
	 * joined them yet.
	 * <p/>
	 * This method does not do anything and should be overridden.
	 *
	 * @param server the Server the Client just joined
	 *
	 * @see #onChannelJoined(Channel)
	 */
	public void onServerJoined(final Server server) {}

	/**
	 * Executed once the Client successfully joins a Channel.
	 * To be more precise, this is triggered once the Client receive an
	 * echo of the {@link fr.ribesg.alix.api.enums.Command#JOIN} command
	 * from the Server that confirms that the Client has successfully joined
	 * the Channel.
	 * <p/>
	 * This method does not do anything and should be overridden.
	 *
	 * @param channel the Channel the Client just joined
	 */
	public void onChannelJoined(final Channel channel) {}

	/**
	 * Executed when the Client receive a Private Message.
	 * <p/>
	 * This method does not do anything and should be overridden.
	 *
	 * @param fromUser the User that sent the Private Message to the Client
	 * @param message  the message sent to the Client
	 */
	public void onPrivateMessage(final Server server, final String fromUser, final String message) {}

	/**
	 * Executed when the Client sees a message sent in a Channel.
	 * <p/>
	 * This method does not do anything and should be overridden.
	 *
	 * @param channel the Channel the message was sent in
	 * @param author  the User that sent the message
	 * @param message the message sent in the Channel
	 */
	public void onChannelMessage(final Channel channel, final String author, final String message) {}

	/**
	 * Executed every time the Client receives an IRC message.
	 * This is where you can do what's not yet in this API as an "Event".
	 * Please ask for features, or make a Pull Request on Github!
	 * <p/>
	 * This method does not do anything and should be overridden.
	 *
	 * @param server  the Server that sent the IRC Message
	 * @param message the IRC Message sent by the Server
	 *
	 * @see
	 */
	public void onRawIrcMessage(final Server server, final Message message) {}
}
