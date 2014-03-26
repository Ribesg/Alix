package fr.ribesg.alix.internal;

import fr.ribesg.alix.api.Channel;
import fr.ribesg.alix.api.Client;
import fr.ribesg.alix.api.Log;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.Source;
import fr.ribesg.alix.api.enums.Command;
import fr.ribesg.alix.api.enums.Reply;
import fr.ribesg.alix.api.message.IrcPacket;
import fr.ribesg.alix.api.message.PongIrcPacket;
import fr.ribesg.alix.internal.callback.CallbackHandler;
import fr.ribesg.alix.internal.network.ReceivedPacket;
import fr.ribesg.alix.internal.thread.AbstractRepeatingThread;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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
public class InternalMessageHandler extends AbstractRepeatingThread {

	/**
	 * A reference to the Client is always nice to have.
	 */
	private final Client client;

	/**
	 * The Queue of received packets, populated by
	 * {@link fr.ribesg.alix.internal.network.SocketReceiver}
	 */
	private final Queue<ReceivedPacket> packetBuffer;

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
		super("MsgHandler", 50);
		this.client = client;
		this.packetBuffer = new ConcurrentLinkedQueue<>();
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

	public void queue(final Server server, final String packet) {
		this.packetBuffer.add(new ReceivedPacket(server, packet));
	}

	@Override
	public void work() {
		ReceivedPacket packet;
		while ((packet = this.packetBuffer.poll()) != null) {
			this.handleMessage(packet.getSource(), packet.getPacket());
		}
	}

	/**
	 * Handles received messages.
	 *
	 * @param server       the Server the message come from
	 * @param packetString the raw IRC message to handle
	 */
	public void handleMessage(final Server server, final String packetString) {

		Log.debug("DEBUG: Handling Packet " + packetString);

		server.setJoined(true);

		// Parse the IRC Packet
		final IrcPacket packet = IrcPacket.parseMessage(packetString);

		// Callback Handler
		if (this.callbackHandler != null) {
			this.callbackHandler.handle(packet);
		}

		// Raw IRC Packet
		client.onRawIrcMessage(server, packet);

		// Command?
		final boolean isCommand = packet.isValidCommand();
		if (isCommand) {
			final Command cmd = packet.getCommandAsCommand();
			switch (cmd) {
				// TODO Handle NICK Command
				case PING:
					server.send(new PongIrcPacket(packet.getTrail()), true);
					break;
				case JOIN:
				case PART:
					Client.getThreadPool().submit(() -> handleJoinPart(server, cmd == Command.JOIN, packet));
					break;
				case KICK:
					Client.getThreadPool().submit(() -> handleKick(server, packet));
					break;
				case QUIT:
					Client.getThreadPool().submit(() -> handleQuit(server, packet));
					break;
				case PRIVMSG:
					Client.getThreadPool().submit(() -> handlePrivMsg(server, packet));
					break;
				default:
					break;
			}
		}

		// Reply?
		else if (packet.isValidReply()) {
			final Reply rep = packet.getCommandAsReply();
			switch (rep) {
				case RPL_WELCOME:
					server.setConnected(true);
					Client.getThreadPool().submit(server::joinChannels);
					Client.getThreadPool().submit(() -> client.onServerJoined(server));
					break;
				case RPL_TOPIC:
					final String channelName = packet.getParameters()[1];
					final Channel channel = server.getChannel(channelName);
					channel.setTopic(packet.getTrail());
					break;
				case ERR_NICKNAMEINUSE:
				case ERR_NICKCOLLISION:
					Client.getThreadPool().submit(() -> client.switchToBackupName(server));
					break;
				default:
					break;
			}
		} else {
			// Reply code not defined by the RFCs
			Log.warn("Unknown command/reply code: " + packet.getRawCommandString());
		}
	}

	private void handleJoinPart(final Server server, final boolean isJoin, final IrcPacket packet) {
		// Workaround for IRCds using the trail as parameter (Unreal)
		final String channelName = packet.getParameters().length > 0 ? packet.getParameters()[0] : packet.getTrail();
		final Channel channel = server.getChannel(channelName);
		final Source source = packet.getPrefix() == null ? null : packet.getPrefixAsSource(server);
		if (source == null || source.getName().equals(server.getClientNick())) {
			if (isJoin) {
				client.onClientJoinChannel(channel);
			} else {
				client.onClientPartChannel(channel);
			}
		} else {
			if (isJoin) {
				// TODO Fetch info about user (+, @) and add it to the users list
				client.onUserJoinChannel(source, channel);
			} else {
				// TODO Remove user from users list
				client.onUserPartChannel(source, channel);
			}
		}
	}

	private void handleKick(final Server server, final IrcPacket packet) {
		final String channelName = packet.getParameters()[0];
		final String who = packet.getParameters()[1];
		final Channel channel = server.getChannel(channelName);
		final Source source = packet.getPrefix() == null ? null : packet.getPrefixAsSource(server);
		final String reason = packet.getTrail();
		if (server.getClientNick().equals(who)) {
			client.onClientKickedFromChannel(channel, source, reason);
		} else {
			// TODO Remove user from users list
			client.onUserKickedFromChannel(channel, source, reason);
		}
	}

	private void handleQuit(final Server server, final IrcPacket packet) {
		final Source source = packet.getPrefix() == null ? null : packet.getPrefixAsSource(server);
		if (source != null) {
			final String who = source.getName();
			final String reason = packet.getTrail();
			if (server.getClientNick().equals(who)) {
				client.onClientKickedFromServer(server, reason);
				server.setJoined(false);
				server.setConnected(false);
			} else {
				// TODO Remove user from users list (in all channels?)
				client.onUserQuitServer(server, reason);
			}
		}
	}

	private void handlePrivMsg(final Server server, final IrcPacket packet) {
		final Source source = packet.getPrefixAsSource(server);
		final String dest = packet.getParameters()[0];
		if (dest.startsWith("#")) {
			final boolean isBotCommand = client.getCommandManager() != null && client.getCommandManager().isCommand(packet.getTrail());
			final Channel channel = server.getChannel(dest);
			if (isBotCommand) {
				client.getCommandManager().exec(server, channel, source, packet.getTrail(), false);
			} else {
				client.onChannelMessage(channel, source, packet.getTrail());
			}
		} else {
			client.getCommandManager().exec(server, null, source, packet.getTrail(), true);
		}
	}
}
