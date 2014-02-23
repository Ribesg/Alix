package fr.ribesg.alix.network;

import fr.ribesg.alix.api.Client;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.enums.Command;
import fr.ribesg.alix.api.enums.Reply;
import fr.ribesg.alix.api.message.Message;
import fr.ribesg.alix.api.message.PongMessage;
import org.apache.log4j.Logger;

/**
 * This class handles messages internally. An example being more clear than
 * any explanation, this class typically handle PING commands by responding
 * with a PONG command.
 * <p/>
 * If the message has or can be handled externally (understand "by the API
 * user" here), then the handler will make appropriate calls to the Client.
 * <p/>
 * Note that every message will still produce a call to
 * {@link Client#onRawIrcMessage(Server, Message)}.
 *
 * @author Ribesg
 */
public class InternalMessageHandler {

	private static final Logger LOGGER = Logger.getLogger(InternalMessageHandler.class.getName());

	/**
	 * A reference to the Client is always nice to have.
	 */
	private final Client client;

	/**
	 * Constructor
	 *
	 * @param client the Client this Handler relates to
	 */
	/* package */ InternalMessageHandler(final Client client) {
		this.client = client;
	}

	/**
	 * Handles received messages.
	 *
	 * @param server        the Server the message come from
	 * @param messageString the raw IRC message to handle
	 */
	/* package */ void handleMessage(final Server server, final String messageString) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				handleMessageAsync(server, messageString);
			}
		}).start();

	}

	/**
	 * Handles received messages asynchronously.
	 * Used to prevent locking the SocketReceiver.
	 *
	 * @param server        the Server the message come from
	 * @param messageString the raw IRC message to handle
	 */
	private void handleMessageAsync(final Server server, final String messageString) {
		// Parse the Message
		final Message m = Message.parseMessage(messageString);

		// Raw IRC Message
		client.onRawIrcMessage(server, m);

		// Command?
		final boolean isCommand = m.isValidCommand();
		if (isCommand) {
			final Command cmd = m.getCommandAsCommand();
			switch (cmd) {
				case PING:
					server.send(new PongMessage(m.getTrail()));
					break;
				case JOIN:
					client.onChannelJoined(server.getChannel(m.getParameters()[0]));
					break;
				case PRIVMSG:
					final String fromUser = m.getPrefix().substring(0, m.getPrefix().indexOf('!'));
					final String dest = m.getParameters()[0];
					if (dest.startsWith("#")) {
						client.onChannelMessage(server.getChannel(dest), fromUser, m.getTrail());
					} else {
						client.onPrivateMessage(server, fromUser, m.getTrail());
					}
					break;
				default:
					break;
			}
		}

		// Reply?
		else if (m.isValidReply()) {
			final Reply rep = m.getCommandAsReply();
			switch (rep) {
				case RPL_WELCOME:
					server.setConnected(true);
					server.joinChannels();
					client.onServerJoined(server);
					break;
				default:
					break;
			}
		} else {
			// Reply code not defined by the RFCs
			LOGGER.warn("Unknown command/reply code: " + m.getRawCommandString());
		}
	}
}
