package fr.ribesg.alix.internal.network;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.internal.InternalMessageHandler;
import fr.ribesg.alix.internal.thread.AbstractRepeatingThread;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * This class handles receiving packets.
 * TODO: All the docz
 *
 * @author Ribesg
 */
public class SocketReceiver extends AbstractRepeatingThread {

	private static final Logger LOGGER = Logger.getLogger(SocketReceiver.class.getName());

	private final BufferedReader reader;

	private final Server                 server;
	private final InternalMessageHandler packetHandler;

	/* package */ SocketReceiver(final Server server, final BufferedReader reader, final InternalMessageHandler packetHandler) {
		super("S-Receiver", 10);
		this.reader = reader;
		this.server = server;
		this.packetHandler = packetHandler;
	}

	@Override
	public void work() {
		String mes;
		try {
			while ((mes = this.reader.readLine()) != null) {
				LOGGER.debug(server.getUrl() + ':' + server.getPort() + " - RECEIVED MESSAGE: '" + mes + "'");
				this.packetHandler.queue(this.server, mes);
			}
		} catch (final IOException ignored) {
			// readLine() Timeout
		}
	}

	/* package */ void kill() {
		try {
			this.reader.close();
		} catch (final IOException e) {
			LOGGER.error("Failed to close Reader stream", e);
		}
	}
}
