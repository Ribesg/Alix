/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.api;
import fr.ribesg.alix.Tools;
import fr.ribesg.alix.api.bot.command.CommandManager;
import fr.ribesg.alix.api.message.NickIrcPacket;
import fr.ribesg.alix.internal.bot.PingPongTask;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

      if (load()) {
         connectToServers();

         Runtime.getRuntime().addShutdownHook(new Thread(this::kill));
      }
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

      THREAD_POOL.shutdown();
      try {
         THREAD_POOL.awaitTermination(5, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
         Runtime.getRuntime().halt(1);
      }

      Log.info("Exiting.");
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
    * @return false if the Client should stop, true otherwise
    *
    * @see fr.ribesg.alix.TestClient TestClient class for an example
    * implementation
    */
   protected abstract boolean load();

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
    *
    * @return the command manager created
    */
   protected CommandManager createCommandManager(final String commandPrefix, final Set<String> botAdmins) {
      return this.commandManager = new CommandManager(commandPrefix, botAdmins);
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
      this.servers.forEach(Server::connect);

      this.pingPongTask = new PingPongTask(this);
      this.pingPongTask.start();
   }
}
