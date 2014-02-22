package fr.ribesg.alix.impl.connection;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.Tools;
import fr.ribesg.alix.api.enums.Command;
import fr.ribesg.alix.api.enums.Reply;
import fr.ribesg.alix.api.message.Message;
import fr.ribesg.alix.api.message.PongMessage;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author Ribesg
 */
public class SocketReceiver implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(SocketReceiver.class.getName());

	private final BufferedReader reader;

	private final Server server;

	private boolean stopAsked;
	private boolean stopped;

	/* package */ SocketReceiver(final Server server, final BufferedReader reader) {
		this.reader = reader;
		this.server = server;
		this.stopAsked = false;
		this.stopped = true;
	}

	@Override
	public void run() {
		this.stopped = false;
		String mes;
		while (!stopAsked) {
			Tools.pause(100);
			try {
				while ((mes = this.reader.readLine()) != null) {
					LOGGER.debug("RECEIVED MESSAGE: '" + mes + "'");
					handleMessage(mes);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.kill();
		this.stopped = true;
	}

	private void handleMessage(final String message) {
		final Message m = Message.parseMessage(message);

		server.getClient().onRawIrcMessage(server, m);

		// Command?
		boolean isCommand = true;
		try {
			final Command cmd = Command.valueOf(m.getRawCommand().toUpperCase());
			switch (cmd) {
				case PING:
					server.send(new PongMessage(m.getTrail()));
					break;
				case JOIN:
					server.getClient().onChannelJoined(server.getChannel(m.getParameters()[0]));
					break;
				case PRIVMSG:
					final String dest = m.getParameters()[0];
					if (dest.startsWith("#")) {
						server.getClient().onMessageInChannel(server.getChannel(dest), m.getTrail());
					}
					break;
				default:
					break;
			}
		} catch (final IllegalArgumentException e) {
			isCommand = false;
		}

		if (!isCommand) {
			final Reply rep = Reply.getFromCode(m.getRawCommand());
			if (rep != null) {
				switch (rep) {
					case RPL_WELCOME:
						server.setConnected(true);
						server.getClient().onServerJoined(server);
						break;
					default:
						break;
				}
			} else {
				LOGGER.warn("Unknown command/reply code: " + m.getRawCommand());
			}
		}
	}

	/* package */ void askStop() {
		this.stopAsked = true;
	}

	/* package */ boolean isStopped() {
		return this.stopped;
	}

	/* package */ void kill() {
		try {
			this.reader.close();
		} catch (IOException ignored) {
		}
	}
}
