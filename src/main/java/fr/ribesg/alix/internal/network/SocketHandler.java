package fr.ribesg.alix.internal.network;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.message.IrcPacket;
import fr.ribesg.alix.internal.InternalMessageHandler;
import fr.ribesg.alix.internal.network.ssl.SSLSocketFactory;
import fr.ribesg.alix.internal.network.ssl.SSLType;
import org.apache.log4j.Logger;

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

	private final Logger LOGGER = Logger.getLogger(SocketHandler.class.getName());

	private final String  url;
	private final int     port;
	private final SSLType sslType;

	private final Server server;

	private Socket         socket;
	private SocketSender   socketSender;
	private SocketReceiver socketReceiver;

	private Thread senderThread;
	private Thread receiverThread;

	private InternalMessageHandler handler;

	public SocketHandler(final Server server, final String url, final int port) {
		this(server, url, port, SSLType.NONE);
	}

	public SocketHandler(final Server server, final String url, final int port, final SSLType sslType) {
		this.url = url;
		this.port = port;
		this.server = server;
		this.sslType = sslType;
	}

	public InternalMessageHandler getHandler() {
		return handler;
	}

	public void connect() throws IOException {
		switch (this.sslType) {
			case NONE:
				this.socket = new Socket(this.url, this.port);
				break;
			case TRUSTING:
				this.socket = SSLSocketFactory.getTrustingSSLSocket(this.url, this.port);
				break;
			case SECURED:
				this.socket = SSLSocketFactory.getSecuredSSLSocket(this.url, this.port);
				break;
		}

		// Prevent infinite lock on reader.readLine() in SocketReceiver
		this.socket.setSoTimeout(1000);

		final BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));

		this.handler = new InternalMessageHandler(this.server.getClient());

		this.socketSender = new SocketSender(this.server, writer);
		this.socketReceiver = new SocketReceiver(this.server, reader, this.handler);

		this.senderThread = new Thread(this.socketSender);
		this.receiverThread = new Thread(this.socketReceiver);

		this.senderThread.start();
		this.receiverThread.start();
	}

	public boolean hasAnythingToWrite() {
		return this.socketSender.hasAnythingToWrite();
	}

	public void writeRaw(final String message) {
		this.socketSender.write(message);
	}

	public void write(final IrcPacket ircPacket) {
		this.writeRaw(ircPacket.getRawMessage());
	}

	public void writeRawFirst(final String message) {
		this.socketSender.writeFirst(message);
	}

	public void writeFirst(final IrcPacket ircPacket) {
		this.writeRawFirst(ircPacket.getRawMessage());
	}

	public void askStop() {
		this.socketSender.askStop();
		this.socketReceiver.askStop();
	}

	public boolean isStopped() {
		return this.socketSender.isStopped() && this.socketReceiver.isStopped();
	}

	public void kill() {
		try {
			this.receiverThread.join();
		} catch (final InterruptedException e) {
			LOGGER.error("Failed to join on ReceiverThread", e);
		}

		try {
			this.senderThread.join();
		} catch (final InterruptedException e) {
			LOGGER.error("Failed to join on SenderThread", e);
		}

		try {
			this.socket.close();
		} catch (final IOException e) {
			LOGGER.error("Failed to close Socket", e);
		}
	}
}
