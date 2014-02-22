package fr.ribesg.alix.impl.connection;
import fr.ribesg.alix.api.Tools;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Ribesg
 */
public class SocketSender implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(SocketSender.class.getName());

	private final BufferedWriter                writer;
	private final ConcurrentLinkedQueue<String> buffer;

	private boolean stopAsked;
	private boolean stopped;

	/* package */ SocketSender(final BufferedWriter writer) {
		this.writer = writer;
		this.buffer = new ConcurrentLinkedQueue<>();
		this.stopAsked = false;
		this.stopped = true;
	}

	public void write(final String message) {
		this.buffer.offer(message);
	}

	@Override
	public void run() {
		this.stopped = false;
		String mes;
		while (!stopAsked) {
			Tools.pause(100);
			try {
				while ((mes = buffer.poll()) != null) {
					Tools.pause(100);
					LOGGER.debug("SENDING MESSAGE: '" + mes + "'");
					writer.write(mes);
				}
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.kill();
		this.stopped = true;
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
		} catch (IOException ignored) {
		}
	}
}
