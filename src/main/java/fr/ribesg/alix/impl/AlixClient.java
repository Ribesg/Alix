package fr.ribesg.alix.impl;
import fr.ribesg.alix.api.Channel;
import fr.ribesg.alix.api.Client;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.message.Message;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ribesg
 */
public class AlixClient implements Client {

	public static void main(String args[]) {
		new AlixClient("AlixTest");
	}

	private final String name;

	private final Set<Server> servers;

	protected AlixClient(final String name) {
		this.name = name;
		this.servers = new HashSet<>();

		loadConfig();

		connectToServers();
	}

	private void loadConfig() {
		final Server server = new Server(this, "irc.esper.net", 5555);

		server.addChannel("#ribesg");

		servers.add(server);
	}

	private void connectToServers() {
		for (final Server server : servers) {
			server.connect(this);
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void onServerJoined(final Server server) {
		server.joinChannels();
	}

	@Override
	public void onChannelJoined(final Channel channel) {
		channel.sendMessage("Hi!");
	}

	@Override
	public void onPrivateMessage(String fromUser, String message) {
		// TODO Implement method
	}

	@Override
	public void onMessageInChannel(final Channel channel, final String message) {
		if (message.startsWith("!say ")) {
			channel.sendMessage(message.substring("!say ".length()));
		}
	}

	@Override
	public void onRawIrcMessage(final Server server, final Message message) {
		// TODO Implement method
	}
}
