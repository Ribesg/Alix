package fr.ribesg.alix.api;

import fr.ribesg.alix.api.message.JoinMessage;
import fr.ribesg.alix.api.message.Message;
import fr.ribesg.alix.api.message.NickMessage;
import fr.ribesg.alix.api.message.UserMessage;
import fr.ribesg.alix.impl.connection.SocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ribesg
 */
public class Server {

	private final Client               client;
	private final String               url;
	private final int                  port;
	private final Map<String, Channel> channels;

	private SocketHandler socket;
	private boolean       connected;

	/**
	 * Main constructor.
	 *
	 * @param url  the url of this Server (IP or FQDN)
	 * @param port the port of this Server
	 */
	public Server(final Client client, final String url, final int port) {
		this.client = client;
		this.url = url;
		this.port = port;
		this.channels = new HashMap<>();
		this.socket = null;
		this.connected = false;
	}

	/**
	 * Constructor with default port 6667
	 *
	 * @param url the url of this Server (IP or FQDN)
	 */
	public Server(final Client client, final String url) {
		this(client, url, 6667);
	}

	public Client getClient() {
		return client;
	}

	public Channel getChannel(final String channelName) {
		return this.channels.get(channelName);
	}

	public void addChannel(final String channelName) {
		this.channels.put(channelName, new Channel(this, channelName));
	}

	public void joinChannels() {
		for (final Channel channel : channels.values()) {
			send(new JoinMessage(channel.getName()));
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

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(final boolean connected) {
		this.connected = connected;
	}

	public void connect(final Client client) {
		if (connected) {
			throw new IllegalStateException("Already Connected!");
		} else {
			this.socket = new SocketHandler(this, this.url, this.port);
			try {
				this.socket.connect();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			this.socket.write(new NickMessage(client.getName()));
			this.socket.write(new UserMessage(client.getName()));
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