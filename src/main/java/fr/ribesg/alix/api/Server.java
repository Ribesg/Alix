package fr.ribesg.alix.api;

import fr.ribesg.alix.api.enums.Command;
import fr.ribesg.alix.api.message.Message;

/** @author Ribesg */
public class Server {

	private final String url;
	private final int    port;

	/**
	 * Main constructor.
	 *
	 * @param url  the url of this Server (IP or FQDN)
	 * @param port the port of this Server
	 */
	public Server(final String url, final int port) {
		this.url = url;
		this.port = port;
	}

	/**
	 * Constructor with default port 6667
	 *
	 * @param url the url of this Server (IP or FQDN)
	 */
	public Server(final String url) {
		this.url = url;
		this.port = 6667;
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
	 * Sends a RAW message to this Receiver.
	 *
	 * @param prefix     The prefix of this message
	 * @param command    The command to send
	 * @param trail      The trailing parameter
	 * @param parameters The parameters for this command
	 */
	public void sendRaw(String prefix, Command command, String trail, String... parameters) {
		this.send(new Message(prefix, command.name(), trail, parameters));
	}

	private void send(final Message message) {
		// TODO Implement stuff
	}
}