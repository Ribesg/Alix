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
 * This class represents an IRC Client.
 * <p>
 * This is the class you should override to start implementing the Alix API.
 *
 * @author Ribesg
 */
public abstract class Client {

	/**
	 * The Client's thread pool. This thread pool is used for every single
	 * asynchronous need of the Client. You can, and you are encouraged to
	 * use it.
	 * <p>
	 * The Cached implementation of the ExecutorService has been chosen
	 * because of the high amount of short-living thread that Alix will
	 * create. In fact, most received IRC Packets will create a new Task.
	 *
	 * @see java.util.concurrent.ExecutorService
	 */
	private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

	/**
	 * The Client's thread pool. This thread pool is used for every single
	 * asynchronous need of the Client. You can, and you are encouraged to
	 * use it.
	 * <p>
	 * The Cached implementation of the ExecutorService has been chosen
	 * because of the high amount of short-living thread that Alix will
	 * create. In fact, most received IRC Packets will create a new Task.
	 *
	 * @return a cached thread pool
	 *
	 * @see java.util.concurrent.ExecutorService
	 */
	public static ExecutorService getThreadPool() {
		return THREAD_POOL;
	}

	/**
	 * Name of this Client, default Nickname used when connecting to Servers
	 */
	protected String name;

	/**
	 * Servers this Client will join or has joined
	 */
	private final Set<Server> servers;

	/**
	 * This Client's CommandManager.
	 *
	 * @see #createCommandManager(String, java.util.Set)
	 */
	private CommandManager commandManager = null;

	/**
	 * The task responsible for Pinging Servers, to make sure the connection
	 * is still active
	 */
	private PingPongTask pingPongTask = null;

	/**
	 * Construct an IRC Client.
	 * <p>
	 * Initialize the {@link #servers} Set, call the {@link #load()} method
	 * then the {@link #connectToServers()} method.
	 * Also adds a shutdown hook to try to exit properly.
	 *
	 * @param name the name of the Client
	 */
	protected Client(final String name) {
		this.name = name;
		this.servers = new HashSet<>();

		load();
		connectToServers();

		Runtime.getRuntime().addShutdownHook(new Thread(this::kill));
	}

	/**
	 * Method called by the shutdown hook.
	 * <p>
	 * This method will try to disconnect from servers and will kill tasks.
	 */
	public void kill() {
		Log.debug("Killing Client...");
		servers.stream().filter(Server::isConnected).forEach(server -> {
			Log.debug("- Disconnecting from " + server.getUrl() + ":" + server.getPort() + "...");
			server.disconnect();
		});
		for (final Server server : servers) {
			while (server.isConnected()) {
				Tools.pause(50);
			}
			Log.debug("- Disconnected from " + server.getUrl() + ":" + server.getPort() + "!");
		}
		this.pingPongTask.askStop();
		try {
			Log.debug("Stopping PingPongTask Thread...");
			this.pingPongTask.join();
		} catch (final InterruptedException ignored) {}

		Log.info("Exiting.");
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
	 *
	 * @param server the Server for which we need a backup name
	 *
	 * @see #getBackupName(Server)
	 */
	public final void switchToBackupName(final Server server) {
		final String newName = getBackupName(server);
		server.setClientNick(newName);
		server.send(new NickIrcPacket(newName), true);
	}

	/**
	 * Create a backup name for the provided Server.
	 * <p>
	 * The default behaviour is to add "_" to the current name until
	 * it gets accepted.
	 * <p>
	 * This can be overriden to define custom secondary nicknames.
	 *
	 * @param server the Server for which we need a backup name
	 *
	 * @return a new name for this Client on the provided Server
	 */
	protected String getBackupName(final Server server) {
		return server.getClientNick() + '_';
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
	 * <p>
	 * If you want to make a bot and you want to use the
	 * CommandManager, you need to call
	 * {@link #createCommandManager(String, Set)} then you can start
	 * registering your Commands with {@link #getCommandManager()} and
	 * {@link CommandManager#registerCommand(fr.ribesg.alix.api.bot.command.Command)}.
	 * <p>
	 * After calling this method, the Client will try to
	 * connect to all servers ({@link #connectToServers()})
	 *
	 * @see fr.ribesg.alix.TestClient TestClient class for an example
	 * implementation
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
	 * @see Server#joinChannels() Joining Channels
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
	 * <p>
	 * To be more precise, this is triggered once the Client receive
	 * the welcome message
	 * ({@link fr.ribesg.alix.api.enums.Reply#RPL_WELCOME}) from the Server.
	 * At this point the Client has already ask to joined defined Channels,
	 * but has not joined them yet.
	 * <p>
	 * This method does not do anything and should be overridden.
	 *
	 * @param server the Server the Client just joined
	 *
	 * @see #onClientJoinChannel(Channel) Doing stuff on Channel join
	 */
	public void onServerJoined(final Server server) {}

	/**
	 * Executed once the Client successfully joins a Channel.
	 * <p>
	 * To be more precise, this is triggered once the Client receive an
	 * echo of the {@link fr.ribesg.alix.api.enums.Command#JOIN} command
	 * from the Server that confirms that the Client has successfully joined
	 * the Channel.
	 * <p>
	 * This method does not do anything and should be overridden.
	 * <p>
	 * Important Note: If you need to interact with the list of users, please use the
	 * {@link Channel#updateUsers(boolean)} method with the
	 * <code>blocking</code> parameter set to <code>true</code> before.
	 *
	 * @param channel the Channel the Client just joined
	 */
	public void onClientJoinChannel(final Channel channel) {}

	/**
	 * Executed once the Client parts a Channel.
	 * <p>
	 * To be more precise, this is triggered once the Client receive an
	 * echo of the {@link fr.ribesg.alix.api.enums.Command#PART} command
	 * from the Server that confirms that the Client has successfully parted
	 * the Channel.
	 * <p>
	 * This method does not do anything and should be overridden.
	 * <p>
	 * Important Note: If you need to interact with the list of users, please use the
	 * {@link Channel#updateUsers(boolean)} method with the
	 * <code>blocking</code> parameter set to <code>true</code> before.
	 *
	 * @param channel the Channel the Client just left
	 */
	public void onClientPartChannel(final Channel channel) {}

	/**
	 * Executed once the Client gets kicked from a Channel.
	 * To be more precise, this is triggered once the Client receive a
	 * {@link fr.ribesg.alix.api.enums.Command#KICK} command with the
	 * Client's name as second parameter.
	 * <p>
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
	 * <p>
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
	 * <p>
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
	 * <p>
	 * This method does not do anything and should be overridden.
	 * <p>
	 * Important Note: If you need to interact with the list of users, please use the
	 * {@link Channel#updateUsers(boolean)} method with the
	 * <code>blocking</code> parameter set to <code>true</code> before.
	 *
	 * @param channel the Channel the User just joined
	 */
	public void onUserJoinChannel(final Source source, final Channel channel) {}

	/**
	 * Executed once a User parts a Channel.
	 * To be more precise, this is triggered once the Client receive a
	 * {@link fr.ribesg.alix.api.enums.Command#PART} command
	 * from the Server with a User set as Prefix.
	 * <p>
	 * This method does not do anything and should be overridden.
	 * <p>
	 * Important Note: If you need to interact with the list of users, please use the
	 * {@link Channel#updateUsers(boolean)} method with the
	 * <code>blocking</code> parameter set to <code>true</code> before.
	 *
	 * @param channel the Channel the User just left
	 */
	public void onUserPartChannel(final Source source, final Channel channel) {}

	/**
	 * Executed once a User gets kicked from a Channel.
	 * To be more precise, this is triggered once the Client receive a
	 * {@link fr.ribesg.alix.api.enums.Command#KICK} command with another
	 * name than the Client's name as second parameter.
	 * <p>
	 * This method does not do anything and should be overridden.
	 * <p>
	 * Important Note: If you need to interact with the list of users, please use the
	 * {@link Channel#updateUsers(boolean)} method with the
	 * <code>blocking</code> parameter set to <code>true</code> before.
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
	 * <p>
	 * This method does not do anything and should be overridden.
	 *
	 * @param server the Server a User just quited or got kicked from
	 * @param reason the reason for the quit/kick
	 */
	public void onUserQuitServer(final Server server, final String reason) {}

	/**
	 * Executed when the Client receive a Private Message.
	 * <p>
	 * This method does not do anything and should be overridden.
	 *
	 * @param fromSource the Source that sent the Private Message to the Client
	 * @param message    the message sent to the Client
	 */
	public void onPrivateMessage(final Server server, final Source fromSource, final String message) {}

	/**
	 * Executed when the Client sees a message sent in a Channel.
	 * <p>
	 * This method does not do anything and should be overridden.
	 * <p>
	 * Important Note: If you need to interact with the list of users, please use the
	 * {@link Channel#updateUsers(boolean)} method with the
	 * <code>blocking</code> parameter set to <code>true</code> before.
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
	 * <p>
	 * This method does not do anything and should be overridden.
	 *
	 * @param server    the Server that sent the IRC Packet
	 * @param ircPacket the IRC Packet sent by the Server
	 */
	public void onRawIrcMessage(final Server server, final IrcPacket ircPacket) {}
}
