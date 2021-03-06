/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.api;

import fr.ribesg.alix.Tools;
import fr.ribesg.alix.api.callback.Callback;
import fr.ribesg.alix.api.event.EventHandler;
import fr.ribesg.alix.api.event.EventHandlerPriority;
import fr.ribesg.alix.api.event.FailedToJoinServerEvent;
import fr.ribesg.alix.api.event.ServerJoinEvent;
import fr.ribesg.alix.api.message.IrcPacket;
import fr.ribesg.alix.api.message.NickIrcPacket;
import fr.ribesg.alix.api.message.PassIrcPacket;
import fr.ribesg.alix.api.message.QuitIrcPacket;
import fr.ribesg.alix.api.message.UserIrcPacket;
import fr.ribesg.alix.api.network.ssl.SSLType;
import fr.ribesg.alix.internal.network.SocketHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Ribesg
 */
public class Server {

   /**
    * A useful reference to the Client
    */
   private final Client client;

   /**
    * The Server's name
    */
   private final String name;

   /**
    * The url used to connect to this server
    * May be a hostname or an IP
    */
   private final String url;

   /**
    * The port used to connect to this server
    * Default: 6667
    */
   private final int port;

   /**
    * The Server's password, if any
    */
   private final String password;

   /**
    * If this Server should be joined with secured SSL, trusting SSL
    * or no SSL
    */
   private final SSLType sslType;

   /**
    * Channels on which the Client is connected or
    * will be connected on this Server
    */
   private final Map<String, Channel> channels;

   /**
    * The client nick on this Server
    */
   private String clientNick;

   /**
    * The client username on this Server
    */
   private String clientUserName;

   /**
    * The SocketHandler dedicated to this Server
    */
   private SocketHandler socket;

   /**
    * Store if this Client has already received a message from this
    * Server or not. Set to true before {@link #connected}.
    */
   private boolean joined;

   /**
    * Store if the Client is connected to this Server or not
    */
   private boolean connected;

   /**
    * Store if the Client started a Server quit action
    */
   private boolean leaving;

   /**
    * Main constructor.
    *
    * @param client         the Client this Server is / will be connected to
    * @param serverName     the Server's name
    * @param clientNick     the Client name on this Server
    * @param clientUserName the Client username on this Server
    * @param url            the url of this Server (IP or FQDN)
    * @param port           the port of this Server
    * @param password       the Server's password
    * @param sslType        If this connection should use secured SSL, trusting SSL
    *                       or no SSL
    */
   public Server(final Client client, final String serverName, final String clientNick, final String clientUserName, final String url, final int port, final String password, final SSLType sslType) {
      this.client = client;
      this.name = serverName;
      this.clientNick = clientNick;
      this.clientUserName = clientUserName;
      this.url = url;
      this.port = port;
      this.password = password;
      this.sslType = sslType;
      this.channels = new HashMap<>();
      this.socket = null;
      this.connected = false;
      EventManager.register(this);
   }

   /**
    * Convenient constructor for password-free connection.
    *
    * @param client         the Client this Server is / will be connected to
    * @param serverName     the Server's name
    * @param clientNick     the Client name on this Server
    * @param clientUserName the Client username on this Server
    * @param url            the url of this Server (IP or FQDN)
    * @param port           the port of this Server
    * @param sslType        If this connection should use secured SSL, trusting SSL
    *                       or no SSL
    */
   public Server(final Client client, final String serverName, final String clientNick, final String clientUserName, final String url, final int port, final SSLType sslType) {
      this(client, serverName, clientNick, clientUserName, url, port, null, sslType);
   }

   /**
    * Convenient constructor for SSL-free connection.
    *
    * @param client     the Client this Server is / will be connected to
    * @param serverName the Server's name
    * @param url        the url of this Server (IP or FQDN)
    * @param port       the port of this Server
    * @param password   the Server's password
    */
   public Server(final Client client, final String serverName, final String url, final int port, final String password) {
      this(client, serverName, client == null ? "AlixTestBot" : client.getName(), client == null ? "AlixTestBot" : client.getName(), url, port, password, SSLType.NONE);
   }

   /**
    * Convenient constructor for SSL-free and password-free connection.
    *
    * @param client     the Client this Server is / will be connected to
    * @param serverName the Server's name
    * @param url        the url of this Server (IP or FQDN)
    * @param port       the port of this Server
    */
   public Server(final Client client, final String serverName, final String url, final int port) {
      this(client, serverName, client == null ? "AlixTestBot" : client.getName(), client == null ? "AlixTestBot" : client.getName(), url, port, null, SSLType.NONE);
   }

   /**
    * @return the Client
    */
   public Client getClient() {
      return client;
   }

   /**
    * @return the Server's name
    */
   public String getName() {
      return name;
   }

   /**
    * @return the Client nick on this Server
    */
   public String getClientNick() {
      return clientNick;
   }

   /**
    * You should not use this. This is called internally when
    * the Server sends a {@link fr.ribesg.alix.api.enums.Command#NICK}
    * Command.
    * <p>
    * To change this Client's nick on this Server, send a
    * {@link fr.ribesg.alix.api.enums.Command#NICK} Command to this Server.
    *
    * @param clientNick the new Client name on this Server
    */
   public void setClientNick(final String clientNick) {
      this.clientNick = clientNick;
   }

   /**
    * @return the Client username on this Server
    */
   public String getClientUserName() {
      return clientUserName;
   }

   /**
    * Gets a Channel object from its name.
    *
    * @param channelName the name of the Channel
    *
    * @return a Channel object, or null
    */
   public Channel getChannel(final String channelName) {
      return this.channels.get(channelName.toLowerCase());
   }

   /**
    * Gets all known Channels for this Server.
    *
    * @return all known Channels for this Server
    */
   public Collection<Channel> getChannels() {
      return channels.values();
   }

   /**
    * Gets all joined Channels for this Server.
    *
    * @return all joined Channels for this Server
    */
   public Set<Channel> getJoinedChannels() {
      return channels.values().stream().filter(Channel::isJoined).collect(Collectors.toSet());
   }

   /**
    * Adds a Channel to the Set of Channels for this Server.
    *
    * @param channelName the name of the Channel to add
    *
    * @return the new Channel
    */
   public Channel addChannel(final String channelName) {
      final Channel channel = new Channel(this, channelName);
      this.channels.put(channelName.toLowerCase(), channel);
      return channel;
   }

   /**
    * Adds a password-protected Channel to the Set of Channels for
    * this Server.
    *
    * @param channelName the name of the Channel to add
    * @param password    the password of the Channel to add
    *
    * @return the new Channel
    */
   public Channel addChannel(final String channelName, final String password) {
      final Channel channel = new Channel(this, channelName, password);
      this.channels.put(channelName.toLowerCase(), channel);
      return channel;
   }

   /**
    * Removes a Channel from the Set of Channels for this Server.
    * Note: Doesn't part from this Channel nor do anything, use with
    * caution.
    *
    * @param channelName the Channel to remove from the Set
    *
    * @return the removed Channel
    */
   public Channel removeChannel(final String channelName) {
      return this.channels.remove(channelName.toLowerCase());
   }

   /**
    * Sends a JOIN Command for every Channels in the Set
    */
   public void joinChannels() {
      if (!connected) {
         throw new IllegalStateException("Not Connected!");
      }
      for (final Channel channel : channels.values()) {
         channel.join();
      }
   }

   @EventHandler(priority = EventHandlerPriority.INTERNAL)
   public void onServerJoined(final ServerJoinEvent event) {
      this.joinChannels();
   }

   /**
    * Gets the URL of this Server.
    * IT could be either an IP or a FQDN.
    *
    * @return the URL of this Server
    */
   public String getUrl() {
      return this.url;
   }

   /**
    * Gets the port of this Server.
    *
    * @return the port of this Server
    */
   public int getPort() {
      return port;
   }

   /**
    * Gets the Server's password, if any.
    *
    * @return the Server's password, if any, null otherwise
    */
   public String getPassword() {
      return password;
   }

   /**
    * Gets if this connection should use secured SSL,
    * trusting SSL or no SSL.
    *
    * @return if this connection should use secured SSL,
    * trusting SSL or no SSL
    */
   public SSLType getSslType() {
      return sslType;
   }

   /**
    * @return true if the Client has joined this Server, i.e. if the Client
    * received at least one message from this Server (and is still connected
    * of course), false otherwise
    */
   public boolean hasJoined() {
      return joined;
   }

   /**
    * Modifies the joined state of this Server.
    * This is called by the
    * {@link fr.ribesg.alix.internal.ReceivedPacketHandler}, please
    * do not use it.
    * <p>
    * This is nothing more than a Setter for {@link #joined}, please
    * use {@link #connect()} and {@link #disconnect()}.
    *
    * @param joined the value wanted for the connected state
    */
   public void setJoined(final boolean joined) {
      this.joined = joined;
   }

   /**
    * @return true if the Client is connected to this Server,
    * false otherwise
    */
   public boolean isConnected() {
      return connected;
   }

   /**
    * Modifies the connected state of this Server.
    * This is called by the
    * {@link fr.ribesg.alix.internal.ReceivedPacketHandler}, please
    * do not use it.
    * <p>
    * This is nothing more than a Setter for {@link #connected}, please
    * use {@link #connect()} and {@link #disconnect()}.
    *
    * @param connected the value wanted for the connected state
    */
   public void setConnected(final boolean connected) {
      this.connected = connected;
   }

   /**
    * @return true if the Client started a Server quitting procedure,
    * false otherwise
    */
   public boolean isLeaving() {
      return leaving;
   }

   /**
    * Connects the Client to the Server.
    * This is a non-blocking method.
    * <p>
    * Note: The Client is <strong>not</strong> connected directly after this method call.
    */
   public void connect() {
      Log.info("Connecting to " + this.url + ":" + this.port + "...");

      if (connected) {
         throw new IllegalStateException("Already Connected!");
      } else {
         this.socket = new SocketHandler(this, this.url, this.port, this.sslType);
         try {
            this.socket.connect();
         } catch (final IOException e) {
            EventManager.call(new FailedToJoinServerEvent(this, e));
            return;
         }
         if (getPassword() != null) {
            this.socket.write(new PassIrcPacket(getPassword()));
         }
         this.socket.write(new NickIrcPacket(getClientNick()));
         this.socket.write(new UserIrcPacket(getClientUserName(), client.getName()));

         Log.info("Successfully connected to " + this.url + ":" + this.port);
         Log.info("Waiting for Welcome message...");
      }
   }

   /**
    * Disconnects the Client from the Server.
    * This is a blocking method.
    * <p>
    * Note: The Client is disconnected directly after this method call.
    */
   public void disconnect() {
      disconnect("Working on the future");
   }

   /**
    * Disconnects the Client from the Server.
    * This is a blocking method.
    * <p>
    * Note: The Client is disconnected directly after this method call.
    *
    * @param message the quit message to send to the server
    */
   public void disconnect(String message) {
      Log.info("Disconnecting from " + this.url + ":" + this.port + "...");

      if (!connected) {
         throw new IllegalStateException("Not Connected!");
      } else {
         this.leaving = true;

         // Sending quit message
         this.socket.write(new QuitIrcPacket(message));

         // Waiting for everything that has to be sent
         while (this.socket.hasAnythingToWrite()) {
            Tools.pause(100);
         }

         // Asking stop
         this.socket.askStop();

         // Waiting maximum of 5 seconds
         int i = 0;
         while (!this.socket.isStopped() && i++ < 50) {
            Tools.pause(100);
         }

         // Killing the SocketHandler
         this.socket.kill();

         this.connected = false;
         this.joined = false;
         this.leaving = false;

         Log.info("Successfully disconnected from " + this.url + ":" + this.port);
      }
   }

   /**
    * Sends an IRC Packet to this Server.
    *
    * @param ircPacket the IRC Packet to be sent
    */
   public void send(final IrcPacket ircPacket) {
      this.send(ircPacket, false, null);
   }

   /**
    * Sends an IRC Packet to this Server.
    *
    * @param ircPacket   the IRC Packet to be sent
    * @param prioritized if this IRC Packet should be sent before other
    *                    already queued packets
    */
   public void send(final IrcPacket ircPacket, final boolean prioritized) {
      this.send(ircPacket, prioritized, null);
   }

   /**
    * Sends an IRC Packet to this Server.
    *
    * @param ircPacket the IRC Packet to be sent
    *                  already queued packets
    * @param callback  a Callback for this IRC Packet
    */
   public void send(final IrcPacket ircPacket, final Callback callback) {
      this.send(ircPacket, false, callback);
   }

   /**
    * Sends an IRC Packet to this Server.
    *
    * @param ircPacket   the IRC Packet to be sent
    * @param prioritized if this IRC Packet should be sent before other
    *                    already queued packets
    * @param callback    a Callback for this IRC Packet
    */
   public void send(final IrcPacket ircPacket, final boolean prioritized, final Callback callback) {
      if (callback != null) {
         callback.setServer(this);
         callback.setOriginalIrcPacket(ircPacket);
         EventManager.register(callback);
      }

      this.sendRaw(ircPacket.getRawMessage(), prioritized);
   }

   /**
    * Sends a RAW message to this Server.
    *
    * @param message     the String message to be sent
    * @param prioritized if this IRC Packet should be sent before other
    *                    already queued packets
    */
   private void sendRaw(final String message, final boolean prioritized) {
      if (this.socket == null) {
         throw new IllegalStateException("Not connected!");
      } else if (prioritized) {
         this.socket.writeRawFirst(message);
      } else {
         this.socket.writeRaw(message);
      }
   }
}