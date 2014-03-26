package fr.ribesg.alix.internal.network;
import fr.ribesg.alix.api.Log;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.message.IrcPacket;
import fr.ribesg.alix.api.network.ssl.SSLType;
import fr.ribesg.alix.internal.InternalMessageHandler;
import fr.ribesg.alix.internal.network.ssl.SSLSocketFactory;

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

	private final String  url;
	private final int     port;
	private final SSLType sslType;

	private final Server server;

	private Socket         socket;
	private SocketSender   socketSender;
	private SocketReceiver socketReceiver;

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
		this.socket.setSoTimeout(1_000);

		final BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
		final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), "UTF-8"));

		this.handler = new InternalMessageHandler(this.server.getClient());

		this.socketSender = new SocketSender(this.server, writer);
		this.socketReceiver = new SocketReceiver(this.server, reader, this.handler);

		this.socketSender.start();
		this.socketReceiver.start();
		this.handler.start();
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
		this.handler.askStop();
	}

	public boolean isStopped() {
		return this.socketSender.isInterrupted() && this.socketReceiver.isInterrupted();
	}

	public void kill() {
		try {
			this.socketReceiver.join();
		} catch (final InterruptedException e) {
			Log.error("Failed to join on SocketReceiver", e);
		}

		try {
			this.socketSender.join();
		} catch (final InterruptedException e) {
			Log.error("Failed to join on SocketSender", e);
		}

		try {
			this.handler.join();
		} catch (final InterruptedException e) {
			Log.error("Failed to join on InternalMessageHandler", e);
		}

		try {
			this.socket.close();
		} catch (final IOException e) {
			Log.error("Failed to close Socket", e);
		}
	}
}
