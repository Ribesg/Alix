package fr.ribesg.alix.api;

import fr.ribesg.alix.Tools;
import fr.ribesg.alix.api.message.JoinMessage;
import fr.ribesg.alix.api.message.Message;
import fr.ribesg.alix.api.message.NickMessage;
import fr.ribesg.alix.api.message.QuitMessage;
import fr.ribesg.alix.api.message.UserMessage;
import fr.ribesg.alix.network.SocketHandler;
import fr.ribesg.alix.network.ssl.SSLType;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ribesg
 */
public class Server {

	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

	/**
	 * A useful reference to the Client
	 */
	private final Client client;

	/**
	 * The url used to connect to this server
	 * May be a hostname or an IP
	 */
	private final String url;

	/**
	 * The port used to connect to this server
	 * Default: 6667
	 */
	private final int port;

	/**
	 * If this Server should be joined with secured SSL, trusting SSL
	 * or no SSL
	 */
	private final SSLType sslType;

	/**
	 * Channels on which the Client is connected or
	 * will be connected on this Server
	 */
	private final Map<String, Channel> channels;

	/**
	 * The SocketHandler dedicated to this Server
	 */
	private SocketHandler socket;

	/**
	 * Store if the Client is connected to this Server or not
	 */
	private boolean connected;

	/**
	 * Main constructor.
	 *
	 * @param client  the Client this Server is / will be connected to
	 * @param url     the url of this Server (IP or FQDN)
	 * @param port    the port of this Server
	 * @param sslType If this connection should use secured SSL, trusting SSL
	 *                or no SSL
	 */
	public Server(final Client client, final String url, final int port, final SSLType sslType) {
		this.client = client;
		this.url = url;
		this.port = port;
		this.sslType = sslType;
		this.channels = new HashMap<>();
		this.socket = null;
		this.connected = false;
	}

	/**
	 * Convenient constructor for SSL-free connection.
	 *
	 * @param client the Client this Server is / will be connected to
	 * @param url    the url of this Server (IP or FQDN)
	 * @param port   the port of this Server
	 */
	public Server(final Client client, final String url, final int port) {
		this(client, url, port, SSLType.NONE);
	}

	/**
	 * @return the Client
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * Gets a Channel object from its name.
	 *
	 * @param channelName the name of the Channel
	 *
	 * @return a Channel object, or null
	 */
	public Channel getChannel(final String channelName) {
		return this.channels.get(channelName);
	}

	/**
	 * Adds a Channel to the Set of Channels for this Server
	 *
	 * @param channelName the name of the Channel to add
	 */
	public void addChannel(final String channelName) {
		this.channels.put(channelName, new Channel(this, channelName));
	}

	/**
	 * Adds a password-protected Channel to the Set of Channels for
	 * this Server
	 *
	 * @param channelName the name of the Channel to add
	 * @param password    the password of the Channel to add
	 */
	public void addChannel(final String channelName, final String password) {
		this.channels.put(channelName, new Channel(this, channelName, password));
	}

	/**
	 * Sends a JOIN Command for every Channels in the Set
	 */
	public void joinChannels() {
		if (!connected) {
			throw new IllegalStateException("Not Connected!");
		}
		for (final Channel channel : channels.values()) {
			if (channel.hasPassword()) {
				send(new JoinMessage(channel.getName(), channel.getPassword()));
			} else {
				send(new JoinMessage(channel.getName()));
			}
		}
	}

	/**
	 * Gets the URL of this Server.
	 * IT could be either an IP or a FQDN.
	 *
	 * @return the URL of this Server
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Gets the port of this Server.
	 *
	 * @return the port of this Server
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Gets if this connection should use secured SSL,
	 * trusting SSL or no SSL.
	 *
	 * @return if this connection should use secured SSL,
	 * trusting SSL or no SSL
	 */
	public SSLType getSslType() {
		return sslType;
	}

	/**
	 * @return true if the Client is connected to this Server,
	 * false otherwise
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Modifies the connected state of this Server.
	 * This is called by the
	 * {@link fr.ribesg.alix.network.InternalMessageHandler}, please
	 * do not use it.
	 * <p/>
	 * This is nothing more than a Setter for {@link #connected}, please
	 * use {@link #connect()} and {@link #disconnect()}.
	 *
	 * @param connected the value wanted for the connected state
	 */
	public void setConnected(final boolean connected) {
		this.connected = connected;
	}

	/**
	 * Connects the Client to the Server.
	 * This is a non-blocking method.
	 * <p/>
	 * Note: The Client is <strong>not</strong> connected directly after this method call.
	 */
	public void connect() {
		LOGGER.info("Connecting to " + this.url + ":" + this.port + "...");

		if (connected) {
			throw new IllegalStateException("Already Connected!");
		} else {
			this.socket = new SocketHandler(this, this.url, this.port, this.sslType);
			try {
				this.socket.connect();
			} catch (final IOException e) {
				LOGGER.error("Failed to connect to Server", e);
				return;
			}
			this.socket.write(new NickMessage(client.getName()));
			this.socket.write(new UserMessage(client.getName()));

			LOGGER.info("Successfully connected to " + this.url + ":" + this.port);
			LOGGER.info("Waiting for Welcome message...");
		}
	}

	/**
	 * Disconnects the Client from the Server.
	 * This is a blocking method.
	 * <p/>
	 * Note: The Client is disconnected directly after this method call.
	 */
	public void disconnect() {
		LOGGER.info("Disconnecting from " + this.url + ":" + this.port + "...");

		if (!connected) {
			throw new IllegalStateException("Not Connected!");
		} else {
			// Sending quit message
			this.socket.write(new QuitMessage("Working on the future"));

			// Waiting for everything that has to be sent
			while (this.socket.hasAnythingToWrite()) {}

			// Asking stop
			this.socket.askStop();

			// Waiting maximum of 5 seconds
			int i = 0;
			while (!this.socket.isStopped() && i++ < 50) {
				Tools.pause(100);
			}

			// Killing the SocketHandler
			this.socket.kill();

			LOGGER.info("Successfully disconnected from " + this.url + ":" + this.port);
		}
	}

	/**
	 * Sends a RAW message to this Receiver.
	 *
	 * @param message the String message to be sent
	 */
	public void sendRaw(final String message) {
		if (this.socket == null) {
			throw new IllegalStateException("Not connected!");
		} else {
			this.socket.writeRaw(message);
		}
	}

	/**
	 * Sends a message to this Server.
	 *
	 * @param message the message to be sent
	 */
	public void send(final Message message) {
		this.sendRaw(message.getRawMessage());
	}
}