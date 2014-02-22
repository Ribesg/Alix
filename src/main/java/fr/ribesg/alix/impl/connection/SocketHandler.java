package fr.ribesg.alix.impl.connection;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.message.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * @author Ribesg
 */
public class SocketHandler {

	private final String url;
	private final int    port;

	private final Server server;

	private Socket         socket;
	private SocketSender   socketSender;
	private SocketReceiver socketReceiver;

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

		final Thread senderThread = new Thread(socketSender);
		final Thread receiverThread = new Thread(socketReceiver);

		senderThread.start();
		receiverThread.start();
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
		this.socketSender.kill();
		this.socketReceiver.kill();
		try {
			this.socket.close();
		} catch (IOException ignored) {
		}
	}
}
