package fr.ribesg.alix.internal.network;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.internal.thread.AbstractRepeatingThread;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * This class handles sending packets.
 * TODO: All the docz
 *
 * @author Ribesg
 */
public class SocketSender extends AbstractRepeatingThread {

	private static final Logger LOGGER = Logger.getLogger(SocketSender.class.getName());

	private final BufferedWriter writer;
	private final Deque<String>  buffer;

	private final Server server;

	/* package */ SocketSender(final Server server, final BufferedWriter writer) {
		super("S-Sender  ", 50);
		this.writer = writer;
		this.buffer = new ConcurrentLinkedDeque<>();
		this.server = server;
	}

	@Override
	public void work() throws InterruptedException {
		String mes;
		try {
			while ((mes = this.buffer.poll()) != null) {
				LOGGER.debug(server.getUrl() + ':' + server.getPort() +
				             " - SENDING MESSAGE: '" + mes.replace("\n", "\\n").replace("\r", "\\r") + "'");
				this.writer.write(mes);
				this.writer.flush();
				Thread.sleep(1_000);
			}
		} catch (final IOException e) {
			LOGGER.error("Failed to send IRC Packet", e);
		}
	}

	public void write(final String message) {
		this.buffer.offer(message);
	}

	public void writeFirst(final String message) {
		this.buffer.offerFirst(message);
	}

	/* package */ boolean hasAnythingToWrite() {
		return !this.buffer.isEmpty();
	}

	/* package */ void kill() {
		try {
			this.writer.close();
		} catch (final IOException e) {
			LOGGER.error("Failed to close Writer stream", e);
		}
	}
}
