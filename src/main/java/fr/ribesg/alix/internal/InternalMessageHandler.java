package fr.ribesg.alix.internal;

import fr.ribesg.alix.api.Channel;
import fr.ribesg.alix.api.Client;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.Source;
import fr.ribesg.alix.api.enums.Command;
import fr.ribesg.alix.api.enums.Reply;
import fr.ribesg.alix.api.message.IrcPacket;
import fr.ribesg.alix.api.message.PongIrcPacket;
import fr.ribesg.alix.internal.callback.CallbackHandler;
import org.apache.log4j.Logger;

/**
 * This class handles messages internally. An example being more clear than
 * any explanation, this class typically handle PING commands by responding
 * with a PONG command.
 * <p>
 * If the message has or can be handled externally (understand "by the API
 * user" here), then the handler will make appropriate calls to the Client.
 * <p>
 * Note that every message will still produce a call to
 * {@link Client#onRawIrcMessage(Server, IrcPacket)}.
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
	 * The Callback handler
	 */
	private CallbackHandler callbackHandler;

	/**
	 * Constructor
	 *
	 * @param client the Client this Handler relates to
	 */
	public InternalMessageHandler(final Client client) {
		this.client = client;
	}

	public void kill() {
		if (this.callbackHandler != null) {
			this.callbackHandler.kill();
		}
	}

	public CallbackHandler getCallbackHandler() {
		if (this.callbackHandler == null) {
			this.callbackHandler = new CallbackHandler();
		}
		return callbackHandler;
	}

	/**
	 * Handles received messages.
	 *
	 * @param server        the Server the message come from
	 * @param messageString the raw IRC message to handle
	 */
	public void handleMessage(final Server server, final String messageString) {
		server.setJoined(true);
		Client.getThreadPool().execute(() -> handleMessageAsync(server, messageString));
	}

	/**
	 * Handles received messages asynchronously.
	 * Used to prevent locking the SocketReceiver.
	 *
	 * @param server       the Server the message come from
	 * @param packetString the raw IRC Packet to handle
	 */
	private void handleMessageAsync(final Server server, final String packetString) {
		// Parse the IRC Packet
		final IrcPacket m = IrcPacket.parseMessage(packetString);

		// Raw IRC Packet
		client.onRawIrcMessage(server, m);

		// Callback Handler
		if (this.callbackHandler != null && this.callbackHandler.handle(m)) {
			return;
		}

		// Command?
		final boolean isCommand = m.isValidCommand();
		if (isCommand) {
			final Command cmd = m.getCommandAsCommand();
			switch (cmd) {
				case PING:
					server.send(new PongIrcPacket(m.getTrail()), true);
					break;
				case JOIN:
				case PART:
					// Workaround for IRCds using the trail as parameter (Unreal)
					String channelName = m.getParameters().length > 0 ? m.getParameters()[0] : m.getTrail();
					Channel channel = server.getChannel(channelName);
					Source source = m.getPrefix() == null ? null : m.getPrefixAsSource(server);
					if (source == null || source.getName().equals(client.getName())) {
						if (cmd == Command.JOIN) {
							client.onClientJoinChannel(channel);
						} else {
							client.onClientPartChannel(channel);
						}
					} else {
						if (cmd == Command.JOIN) {
							// TODO Fetch info about user (+, @) and add it to the users list
							client.onUserJoinChannel(source, channel);
						} else {
							// TODO Remove user from users list
							client.onUserPartChannel(source, channel);
						}
					}
					break;
				case KICK:
					channelName = m.getParameters()[0];
					String who = m.getParameters()[1];
					channel = server.getChannel(channelName);
					source = m.getPrefix() == null ? null : m.getPrefixAsSource(server);
					String reason = m.getTrail();
					if (client.getName().equals(who)) {
						client.onClientKickedFromChannel(channel, source, reason);
					} else {
						// TODO Remove user from users list
						client.onUserKickedFromChannel(channel, source, reason);
					}
					break;
				case QUIT:
					source = m.getPrefix() == null ? null : m.getPrefixAsSource(server);
					if (source != null) {
						who = source.getName();
						reason = m.getTrail();
						if (client.getName().equals(who)) {
							client.onClientKickedFromServer(server, reason);
							server.setJoined(false);
							server.setConnected(false);
						} else {
							// TODO Remove user from users list (in all channels?)
							client.onUserQuitServer(server, reason);
						}
					}
					break;
				case PRIVMSG:
					source = m.getPrefixAsSource(server);
					final String dest = m.getParameters()[0];
					if (dest.startsWith("#")) {
						final boolean isBotCommand = client.getCommandManager() != null && client.getCommandManager().isCommand(m.getTrail());
						channel = server.getChannel(dest);
						if (isBotCommand) {
							client.getCommandManager().exec(server, channel, source, m.getTrail(), false);
						} else {
							client.onChannelMessage(channel, source, m.getTrail());
						}
					} else {
						client.getCommandManager().exec(server, null, source, m.getTrail(), true);
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
				case RPL_TOPIC:
					String channelName = m.getParameters()[1];
					Channel channel = server.getChannel(channelName);
					channel.setTopic(m.getTrail());
					break;
				case ERR_NICKNAMEINUSE:
				case ERR_NICKCOLLISION:
					client.switchToBackupName();
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