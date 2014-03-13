package fr.ribesg.alix.internal.network;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.internal.InternalMessageHandler;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * This class handles receiving packets.
 * TODO: All the docz
 *
 * @author Ribesg
 */
public class SocketReceiver implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(SocketReceiver.class.getName());

	private final BufferedReader reader;

	private final Server                 server;
	private final InternalMessageHandler handler;

	private boolean stopAsked;
	private boolean stopped;

	/* package */ SocketReceiver(final Server server, final BufferedReader reader, final InternalMessageHandler handler) {
		this.reader = reader;
		this.server = server;
		this.handler = handler;
		this.stopAsked = false;
		this.stopped = true;
	}

	@Override
	public void run() {
		this.stopped = false;
		String mes;
		while (!this.stopAsked) {
			try {
				while ((mes = this.reader.readLine()) != null) {
					LOGGER.debug(server.getUrl() + ':' + server.getPort() + " - RECEIVED MESSAGE: '" + mes + "'");
					this.handler.handleMessage(this.server, mes);
				}
			} catch (final IOException ignored) {
				// readLine() Timeout
			}
		}
		this.kill();
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
		} catch (final IOException e) {
			LOGGER.error("Failed to close Reader stream", e);
		}
		this.stopped = true;
	}
}
