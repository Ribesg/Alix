package fr.ribesg.alix.network;
import fr.ribesg.alix.api.Server;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class handles sending packets.
 * TODO: All the docz
 *
 * @author Ribesg
 */
public class SocketSender implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(SocketSender.class.getName());

	private final BufferedWriter                writer;
	private final ConcurrentLinkedQueue<String> buffer;

	private final Server server;

	private boolean stopAsked;
	private boolean stopped;

	/* package */ SocketSender(final Server server, final BufferedWriter writer) {
		this.writer = writer;
		this.buffer = new ConcurrentLinkedQueue<>();
		this.server = server;
		this.stopAsked = false;
		this.stopped = true;
	}

	@Override
	public void run() {
		this.stopped = false;
		String mes;
		while (!this.stopAsked) {
			Tools.pause(50);
			try {
				while ((mes = this.buffer.poll()) != null) {
					LOGGER.debug(server.getUrl() + ':' + server.getPort() +
					             " - SENDING MESSAGE: '" + mes.replace("\n", "\\n").replace("\r", "\\r") + "'");
					this.writer.write(mes);
					Tools.pause(250);
				}
				this.writer.flush();
			} catch (final IOException e) {
				LOGGER.error("Failed to send IRC Packet", e);
			}
		}
		this.kill();
	}

	public void write(final String message) {
		this.buffer.offer(message);
	}

	/* package */ boolean hasAnythingToWrite() {
		return !this.buffer.isEmpty();
	}

	/* package */ void askStop() {
		this.stopAsked = true;
	}

	/* package */ boolean isStopped() {
		return this.stopped;
	}

	/* package */ void kill() {
		try {
			this.writer.close();
		} catch (final IOException e) {
			LOGGER.error("Failed to close Writer stream", e);
		}
		this.stopped = true;
	}
}
