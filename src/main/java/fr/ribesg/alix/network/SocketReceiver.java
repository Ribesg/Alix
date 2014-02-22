package fr.ribesg.alix.network;
import fr.ribesg.alix.Tools;
import fr.ribesg.alix.api.Server;
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

	/* package */ SocketReceiver(final Server server, final BufferedReader reader) {
		this.reader = reader;
		this.server = server;
		this.handler = new InternalMessageHandler(server.getClient());
		this.stopAsked = false;
		this.stopped = true;
	}

	@Override
	public void run() {
		this.stopped = false;
		String mes;
		while (!stopAsked) {
			Tools.pause(SocketHandler.CHECK_DELAY);
			try {
				while ((mes = this.reader.readLine()) != null) {
					LOGGER.debug("RECEIVED MESSAGE: '" + mes + "'");
					handler.handleMessage(server, mes);
				}
			} catch (IOException e) {
				e.printStackTrace();
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
			this.stopped = true;
		} catch (IOException ignored) {
		}
	}
}
