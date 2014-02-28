package fr.ribesg.alix;
import fr.ribesg.alix.api.Channel;
import fr.ribesg.alix.api.Client;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.Source;
import fr.ribesg.alix.api.bot.commands.Command;
import fr.ribesg.alix.api.message.PrivMsgMessage;
import fr.ribesg.alix.network.ssl.SSLType;

/**
 * Example usage of the Alix IRC API.
 * Here, as a bot that does almost nothing.
 *
 * @author Ribesg
 */
public class TestClient {

	public static void main(final String args[]) {
		new Client("AlixTestBot") {

			@Override
			protected void load() {
				final Server server = new Server(this, "irc.esper.net", 6697, SSLType.TRUSTING);
				server.addChannel("#alix");
				this.getServers().add(server);

				this.createCommandManager("!", null);

				this.getCommandManager().registerCommand(new Command("test") {

					@Override
					public void exec(final Server server, final Channel channel, final Source user) {
						if (channel == null) {
							user.sendMessage("Use the !test command in a Channel!");
						} else {
							channel.sendMessage("So " + user.getName() + " used the command " + this.getName() + " in the Channel " + channel.getName() + "!");
						}
					}
				});
			}

			@Override
			public void onServerJoined(final Server server) {
				/* Here you can register with NickServ for example
				server.send(new PrivMsgMessage("NickServ", "REGISTER SomePassword some@email"));
				server.send(new PrivMsgMessage("NickServ", "IDENTIFY SomePassword"));
				*/
			}

			@Override
			public void onAlixJoinChannel(final Channel channel) {
				channel.sendMessage("Hi!");
			}

			@Override
			public void onUserJoinChannel(final Source source, final Channel channel) {
				channel.sendMessage(source.getName() + ", Hi!");
			}

			@Override
			public void onPrivateMessage(final Server server, final Source fromSource, final String message) {
				server.send(new PrivMsgMessage(fromSource.getName(), "Hi!"));
				if (message.equalsIgnoreCase(getName() + ", quit")) {
					// Disconnect from server
					server.disconnect();
				} else if (message.startsWith("!pm ") && message.length() > "!pm ".length() + 1) {
					// Simple command to ask to SomeBot to send a pm to someone
					final String dest = message.split(" ")[1];
					final String mes = message.substring("!pm ".length() + dest.length() + 1);
					server.send(new PrivMsgMessage(dest, mes));
				}
			}

			@Override
			public void onChannelMessage(final Channel channel, final Source fromSource, final String message) {
				if (message.equalsIgnoreCase(getName() + ", quit")) {
					// Disconnect from server
					channel.getServer().disconnect();
				} else if (message.startsWith(getName() + ", ")) {
					// Repeat message
					channel.sendMessage(fromSource.getName() + message.substring(getName().length()));
				}
			}
		};
	}

}
