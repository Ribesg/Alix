package fr.ribesg.alix.network;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.message.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * This class handles all the Network stuff.
 * TODO: All the docz
 *
 * @author Ribesg
 */
public class SocketHandler {

	/* package */ static final int CHECK_DELAY = 100;
	/* package */ static final int SEND_LIMIT  = 50;

	private final String url;
	private final int    port;

	private final Server server;

	private Socket         socket;
	private SocketSender   socketSender;
	private SocketReceiver socketReceiver;

	private Thread senderThread;
	private Thread receiverThread;

	public SocketHandler(final Server server, final String url, final int port) {
		this.url = url;
		this.port = port;
		this.server = server;
	}

	public void connect() throws IOException {
		this.socket = new Socket(this.url, this.port);
		final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

		this.socketSender = new SocketSender(writer);
		this.socketReceiver = new SocketReceiver(server, reader);

		senderThread = new Thread(socketSender);
		receiverThread = new Thread(socketReceiver);

		senderThread.start();
		receiverThread.start();
	}

	public boolean hasAnythingToWrite() {
		return this.socketSender.hasAnythingToWrite();
	}

	public void writeRaw(final String message) {
		this.socketSender.write(message);
	}

	public void write(final Message message) {
		this.writeRaw(message.getRawMessage());
	}

	public void askStop() {
		this.socketSender.askStop();
		this.socketReceiver.askStop();
	}

	public boolean isStopped() {
		return this.socketSender.isStopped() && this.socketReceiver.isStopped();
	}

	public void kill() {
		this.socketReceiver.kill();
		try {
			this.receiverThread.join();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

		this.socketSender.kill();
		try {
			this.senderThread.join();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

		try {
			this.socket.close();
		} catch (IOException ignored) {
		}
	}
}
