package fr.ribesg.alix.internal.bot;
import fr.ribesg.alix.api.Client;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.callback.Callback;
import fr.ribesg.alix.api.enums.Command;
import fr.ribesg.alix.api.message.IrcPacket;
import fr.ribesg.alix.api.message.PingIrcPacket;
import fr.ribesg.alix.internal.thread.AbstractRepeatingThread;

import java.util.Random;

/**
 * This task will handle the Ping-Pong thing, to make sure
 * we're still connected.
 */
public class PingPongTask extends AbstractRepeatingThread {

	private static final Random RANDOM = new Random();

	private final Client client;

	public PingPongTask(final Client client) {
		super("PingPong  ",120_000);
		this.client = client;
	}

	@Override
	public void work() {
		this.client.getServers().stream().filter(Server::isConnected).forEach(server -> {
			final String value = Long.toString(RANDOM.nextLong());
			server.send(new PingIrcPacket(value), new PingPongCallback(value));
		});
	}

	private class PingPongCallback extends Callback {

		private String value;

		private PingPongCallback(final String value) {
			super(60_000, Command.PONG.name());
			this.value = value;
		}

		@Override
		public boolean onIrcPacket(final IrcPacket packet) {
			return this.value.equals(packet.getTrail());
		}

		@Override
		public void onTimeout() {
			this.server.disconnect();
			this.server.getClient().onClientLostConnection(this.server);
		}
	}
}
