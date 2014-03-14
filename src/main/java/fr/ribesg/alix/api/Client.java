package fr.ribesg.alix.api;
import fr.ribesg.alix.Tools;
import fr.ribesg.alix.api.bot.command.CommandManager;
import fr.ribesg.alix.api.message.IrcPacket;
import fr.ribesg.alix.api.message.NickIrcPacket;
import fr.ribesg.alix.internal.bot.PingPongTask;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Represents an IRC Client that can connect to IRC servers,
 * join channels on those various IRC servers, etc.
 *
 * @author Ribesg
 */
public abstract class Client {

	private static final Logger LOGGER = Logger.getLogger(Client.class.getName());

	private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

	public static ExecutorService getThreadPool() {
		return THREAD_POOL;
	}

	/**
	 * Name of this Client, used as Nick
	 */
	private String name;

	/**
	 * Servers this Client will join or has joined
	 */
	private final Set<Server> servers;

	private CommandManager commandManager = null;

	private PingPongTask pingPongTask = null;

	/**
	 * Constructs an IRC Client, call the {@link #load()} method then the
	 * {@link #connectToServers()} method.
	 *
	 * @param name the name of the Client
	 */
	protected Client(final String name) {
		this.name = name;
		this.servers = new HashSet<>();

		load();

		connectToServers();

		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				Client.this.kill();
			}
		});
	}

	public void kill() {
		LOGGER.debug("Killing Client...");
		for (final Server server : servers) {
			if (server.isConnected()) {
				LOGGER.debug("- Disconnecting from " + server.getUrl() + ":" + server.getPort() + "...");
				server.disconnect();
			}
		}
		for (final Server server : servers) {
			while (server.isConnected()) {
				Tools.pause(50);
			}
			LOGGER.debug("- Disconnected from " + server.getUrl() + ":" + server.getPort() + "!");
		}
		this.pingPongTask.kill();
		try {
			LOGGER.debug("Stopping PingPongTask Thread...");
			this.pingPongTask.join();
		} catch (final InterruptedException ignored) {}

		LOGGER.info("Exiting.");
		System.exit(0);
	}

	/**
	 * Gets the name of this Client.
	 *
	 * @return the name of this Client
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * Switch to a backup name.
	 * The default behaviour is to add "Bot" to the current name until
	 * it gets accepted.
	 * <p/>
	 * This can be overriden to define custom secondary nicknames.
	 */
	public void switchToBackupName() {
		this.name += "Bot";
		for (final Server server : this.servers) {
			if (server.hasJoined()) {
				server.send(new NickIrcPacket(this.name));
			}
		}
	}

	/**
	 * Gets the servers this Client will connect to
	 * or is connected to.
	 *
	 * @return the servers this Client will connect to or is connected to
	 */
	public final Set<Server> getServers() {
		return this.servers;
	}

	/**
	 * This method is called by the Constructor.
	 * That's typically where you should load your
	 * config files or ask for user input to populate
	 * the {@link #servers} Set.
	 * <p/>
	 * If you want to make a bot and you want to use the
	 * CommandManager, you need to call
	 * {@link #createCommandManager(String, Set)} then you can start
	 * registering your Commands with {@link #getCommandManager()} and
	 * {@link CommandManager#registerCommand(fr.ribesg.alix.api.bot.command.Command)}.
	 * <p/>
	 * After calling this method, the Client will try to
	 * connect to all servers ({@link #connectToServers()})
	 *
	 * @see fr.ribesg.alix.TestClient for example
	 */
	protected abstract void load();

	/**
	 * Creates a new CommandManager for this Client.
	 * After calling this, please use
	 * {@link CommandManager#registerCommand(fr.ribesg.alix.api.bot.command.Command)}
	 * to register your Commands.
	 *
	 * @param commandPrefix the prefix of every Commands in this
	 *                      CommandManager
	 * @param botAdmins     a Set of nicknames which have to be considered
	 *                      as Admins
	 */
	protected final void createCommandManager(final String commandPrefix, final Set<String> botAdmins) {
		this.commandManager = new CommandManager(commandPrefix, botAdmins);
	}

	/**
	 * @return the CommandManager of this Client, or null if there's none
	 */
	public final CommandManager getCommandManager() {
		return this.commandManager;
	}

	/**
	 * Initialize connection with all configured servers.
	 * Once sockets are opened and everything's fine, the Client
	 * will automagically try to join known channels.
	 *
	 * @see Server#joinChannels()
	 */
	private void connectToServers() {
		for (final Server server : this.servers) {
			server.connect();
		}

		this.pingPongTask = new PingPongTask(this);
		this.pingPongTask.start();
	}

	// ************************************************************************* //
	// ** Below this comment are methods supposed to be overridden by the API ** //
	// ** user for him to be able to do stuff. That's kind of Events, yes!    ** //
	// ************************************************************************* //

	/**
	 * Executed once the Client successfully connects to a Server.
	 * To be more precise, this is triggered once the Client receive
	 * the welcome message
	 * ({@link fr.ribesg.alix.api.enums.Reply#RPL_WELCOME}) from the Server.
	 * At this point the Client asked to joined defined Channels, but has not
	 * joined them yet.
	 * <p/>
	 * This method does not do anything and should be overridden.
	 *
	 * @param server the Server the Client just joined
	 *
	 * @see #onClientJoinChannel(Channel)
	 */
	public void onServerJoined(final Server server) {}

	/**
	 * Executed once the Client successfully joins a Channel.
	 * To be more precise, this is triggered once the Client receive an
	 * echo of the {@link fr.ribesg.alix.api.enums.Command#JOIN} command
	 * from the Server that confirms that the Client has successfully joined
	 * the Channel.
	 * <p/>
	 * This method does not do anything and should be overridden.
	 * <p/>
	 * Note: If you need to interact with the list of users or with the
	 * topic of the Channel here, please wait before doing it. At this point,
	 * those are not set. If they are not set in the next 2 seconds, Alix
	 * will send the appropriate TOPIC and NAMES commands to the server.
	 * So you can either use {@link fr.ribesg.alix.Tools#pause(int)} for
	 * around 3-4 seconds or just make a loop waiting for it to be set,
	 * as everything is async. Please use the pause(int) method in your
	 * waiting loop to prevent surcharging the bot.
	 *
	 * @param channel the Channel the Client just joined
	 */
	public void onClientJoinChannel(final Channel channel) {}

	/**
	 * Executed once the Client parts a Channel.
	 * To be more precise, this is triggered once the Client receive an
	 * echo of the {@link fr.ribesg.alix.api.enums.Command#PART} command
	 * from the Server that confirms that the Client has successfully parted
	 * the Channel.
	 * <p/>
	 * This method does not do anything and should be overridden.
	 *
	 * @param channel the Channel the Client just left
	 */
	public void onClientPartChannel(final Channel channel) {}

	/**
	 * Executed once the Client gets kicked from a Channel.
	 * To be more precise, this is triggered once the Client receive a
	 * {@link fr.ribesg.alix.api.enums.Command#KICK} command with the
	 * Client's name as second parameter.
	 * <p/>
	 * This method tries to rejoin the Channel by default and could be
	 * overridden.
	 *
	 * @param channel the Channel the Client just got kicked from
	 * @param by      the Source of the kick
	 * @param reason  the reason for the kick
	 */
	public void onClientKickedFromChannel(final Channel channel, final Source by, final String reason) {
		Tools.pause(2_500);
		channel.join();
	}

	/**
	 * Executed once the Client gets kicked from a Server.
	 * To be more precise, this is triggered once the Client receive a
	 * {@link fr.ribesg.alix.api.enums.Command#QUIT} command with the
	 * Client's name as prefix.
	 * <p/>
	 * This method tries to rejoin the Server and all Channels by default
	 * and could be overridden.
	 *
	 * @param server the Server the Client just got kicked from
	 * @param reason the reason for the kick
	 */
	public void onClientKickedFromServer(final Server server, final String reason) {
		if (!server.isLeaving()) {
			Tools.pause(2_500);
			server.connect();
		}
	}

	/**
	 * Executed once the Client loose the connection to the Server.
	 * To be more precise, this is triggered if the Client doesn't receive a
	 * {@link fr.ribesg.alix.api.enums.Command#PONG} command within 5 seconds
	 * after sending a {@link fr.ribesg.alix.api.enums.Command#PING} command
	 * to this Server.
	 * <p/>
	 * This method tries to rejoin the Server and all Channels by default
	 * and could be overridden.
	 *
	 * @param server the Server the Client lost the connection to
	 */
	public void onClientLostConnection(final Server server) {
		if (!server.isLeaving()) {
			Tools.pause(2_500);
			server.connect();
		}
	}

	/**
	 * Executed when a User successfully joins a Channel.
	 * To be more precise, this is triggered once the Client receive a
	 * {@link fr.ribesg.alix.api.enums.Command#JOIN} command
	 * from the Server with a User set as Prefix.
	 * <p/>
	 * This method does not do anything and should be overridden.
	 *
	 * @param channel the Channel the User just joined
	 */
	public void onUserJoinChannel(final Source source, final Channel channel) {}

	/**
	 * Executed once a User parts a Channel.
	 * To be more precise, this is triggered once the Client receive a
	 * {@link fr.ribesg.alix.api.enums.Command#PART} command
	 * from the Server with a User set as Prefix.
	 * <p/>
	 * This method does not do anything and should be overridden.
	 *
	 * @param channel the Channel the User just left
	 */
	public void onUserPartChannel(final Source source, final Channel channel) {}

	/**
	 * Executed once a User gets kicked from a Channel.
	 * To be more precise, this is triggered once the Client receive a
	 * {@link fr.ribesg.alix.api.enums.Command#KICK} command with another
	 * name than the Client's name as second parameter.
	 * <p/>
	 * This method does not do anything and should be overridden.
	 *
	 * @param channel the Channel a User just got kicked from
	 * @param by      the Source of the kick
	 */
	public void onUserKickedFromChannel(final Channel channel, final Source by, final String reason) {}

	/**
	 * Executed once a User quits a Server or get kicked from a Server.
	 * To be more precise, this is triggered once the Client receive a
	 * {@link fr.ribesg.alix.api.enums.Command#QUIT} command with another
	 * name than the Client's name as prefix.
	 * <p/>
	 * This method does not do anything and should be overridden.
	 *
	 * @param server the Server a User just quited or got kicked from
	 * @param reason the reason for the quit/kick
	 */
	public void onUserQuitServer(final Server server, final String reason) {}

	/**
	 * Executed when the Client receive a Private Message.
	 * <p/>
	 * This method does not do anything and should be overridden.
	 *
	 * @param fromSource the Source that sent the Private Message to the Client
	 * @param message    the message sent to the Client
	 */
	public void onPrivateMessage(final Server server, final Source fromSource, final String message) {}

	/**
	 * Executed when the Client sees a message sent in a Channel.
	 * <p/>
	 * This method does not do anything and should be overridden.
	 *
	 * @param channel    the Channel the message was sent in
	 * @param fromSource the Source that sent the message
	 * @param message    the message sent in the Channel
	 */
	public void onChannelMessage(final Channel channel, final Source fromSource, final String message) {}

	/**
	 * Executed every time the Client receives an IRC Packet.
	 * This is where you can do what's not yet in this API as an "Event".
	 * Please ask for features, or make a Pull Request on Github!
	 * <p/>
	 * This method does not do anything and should be overridden.
	 *
	 * @param server    the Server that sent the IRC Packet
	 * @param ircPacket the IRC Packet sent by the Server
	 */
	public void onRawIrcMessage(final Server server, final IrcPacket ircPacket) {}
}
