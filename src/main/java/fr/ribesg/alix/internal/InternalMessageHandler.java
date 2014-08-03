/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.internal;

import fr.ribesg.alix.api.*;
import fr.ribesg.alix.api.callback.CallbackPriority;
import fr.ribesg.alix.api.enums.Command;
import fr.ribesg.alix.api.enums.Reply;
import fr.ribesg.alix.api.message.IrcPacket;
import fr.ribesg.alix.api.message.PongIrcPacket;
import fr.ribesg.alix.internal.callback.CallbackHandler;
import fr.ribesg.alix.internal.network.ReceivedPacketEvent;
import fr.ribesg.alix.internal.thread.AbstractRepeatingThread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class handles messages internally. An example being more clear than
 * any explanation, this class typically handle PING commands by responding
 * with a PONG command.
 * <p>
 * If the message has or can be handled externally (understand "by the API
 * user" here), then the handler will make appropriate calls to the Client.
 * <p>
 * Note that every message will still produce a call to
 * {@link Client#onRawIrcMessage(Server, IrcPacket)}.
 *
 * @author Ribesg
 */
public class InternalMessageHandler extends AbstractRepeatingThread {

   /**
    * A reference to the Client is always nice to have.
    */
   private final Client client;

   /**
    * The Queue of received packets, populated by
    * {@link fr.ribesg.alix.internal.network.SocketReceiver}
    */
   private final BlockingQueue<ReceivedPacketEvent> packetBuffer;

   /**
    * The Callback handler
    */
   private CallbackHandler callbackHandler;

   /**
    * Constructor
    *
    * @param client the Client this Handler relates to
    */
   public InternalMessageHandler(final Client client) {
      super("MsgHandler", 50);
      this.client = client;
      this.packetBuffer = new LinkedBlockingQueue<>();
   }

   public void kill() {
      if (this.callbackHandler != null) {
         this.callbackHandler.kill();
      }
   }

   public CallbackHandler getCallbackHandler() {
      if (this.callbackHandler == null) {
         this.callbackHandler = new CallbackHandler();
      }
      return callbackHandler;
   }

   public void queue(final Server server, final String packet) {
      Log.debug("DEBUG: Queue packet " + packet);
      this.packetBuffer.add(new ReceivedPacketEvent(server, packet));
   }

   @Override
   public void work() {
      ReceivedPacketEvent event;
      while ((event = this.packetBuffer.poll()) != null) {
         Log.debug("DEBUG: Poll packet " + event.getPacket());
         this.handleMessage(event);
      }
   }

   /**
    * Handles received messages.
    *
    * @param event the IRC message to handle
    */
   public void handleMessage(final ReceivedPacketEvent event) {
      Log.debug("DEBUG: Handling Event " + event);

      final Server server = event.getServer();
      final IrcPacket packet = event.getPacket();
      server.setJoined(true);

      // Callback Handler: High priorities
      if (this.callbackHandler != null) {
         this.callbackHandler.handle(CallbackPriority.HIGHEST, event);
         this.callbackHandler.handle(CallbackPriority.HIGH, event);
      }

      // Raw IRC Packet
      client.onRawIrcMessage(server, packet);

      if (!event.isConsumed()) {

         // Command?
         final boolean isCommand = packet.isValidCommand();
         if (isCommand) {
            final Command cmd = packet.getCommandAsCommand();
            switch (cmd) {
               // TODO Handle NICK Command
               case PING:
                  server.send(new PongIrcPacket(packet.getTrail()), true);
                  event.consume();
                  break;
               case JOIN:
               case PART:
                  handleJoinPart(server, cmd == Command.JOIN, packet);
                  event.consume();
                  break;
               case KICK:
                  handleKick(server, packet);
                  event.consume();
                  break;
               case QUIT:
                  handleQuit(server, packet);
                  event.consume();
                  break;
               case PRIVMSG:
                  handlePrivMsg(server, packet);
                  event.consume();
                  break;
               default:
                  break;
            }
         }

         // Reply?
         else if (packet.isValidReply()) {
            final Reply rep = packet.getCommandAsReply();
            switch (rep) {
               case RPL_WELCOME:
                  server.setConnected(true);
                  Client.getThreadPool().submit(server::joinChannels);
                  Client.getThreadPool().submit(() -> client.onServerJoined(server));
                  event.consume();
                  break;
               case RPL_TOPIC:
                  final String channelName = packet.getParameters()[1];
                  final Channel channel = server.getChannel(channelName);
                  channel.setTopic(packet.getTrail());
                  event.consume();
                  break;
               case ERR_NICKNAMEINUSE:
               case ERR_NICKCOLLISION:
                  Client.getThreadPool().submit(() -> client.switchToBackupName(server));
                  event.consume();
                  break;
               default:
                  break;
            }
         } else {
            // Reply code not defined by the RFCs
            Log.warn("Unknown command/reply code: " + packet.getRawCommandString());
         }

      }

      // Callback Handler: Low priorities
      if (this.callbackHandler != null) {
         this.callbackHandler.handle(CallbackPriority.LOW, event);
         this.callbackHandler.handle(CallbackPriority.LOWEST, event);
      }

      Log.debug("DEBUG: Packet " + (event.isConsumed() ? "not" : "") + " consumed: " + packet);
   }

   private void handleJoinPart(final Server server, final boolean isJoin, final IrcPacket packet) {
      // Workaround for IRCds using the trail as parameter (Unreal)
      final String channelName = packet.getParameters().length > 0 ? packet.getParameters()[0] : packet.getTrail();
      final Channel channel = server.getChannel(channelName);
      if (channel == null) {
         server.addChannel(channelName);
      }
      final Source source = packet.getPrefix() == null ? null : packet.getPrefixAsSource(server);
      if (source == null || source.getName().equals(server.getClientNick())) {
         if (isJoin) {
            Client.getThreadPool().submit(() -> client.onClientJoinChannel(channel));
         } else {
            Client.getThreadPool().submit(() -> {
               client.onClientPartChannel(channel);
               server.removeChannel(channelName);
            });
         }
      } else {
         if (isJoin) {
            // TODO Fetch info about user (+, @) and add it to the users list
            Client.getThreadPool().submit(() -> client.onUserJoinChannel(source, channel));
         } else {
            // TODO Remove user from users list
            Client.getThreadPool().submit(() -> client.onUserPartChannel(source, channel));
         }
      }
   }

   private void handleKick(final Server server, final IrcPacket packet) {
      final String channelName = packet.getParameters()[0];
      final String who = packet.getParameters()[1];
      final Channel channel = server.getChannel(channelName);
      final Source source = packet.getPrefix() == null ? null : packet.getPrefixAsSource(server);
      final String reason = packet.getTrail();
      if (server.getClientNick().equals(who)) {
         Client.getThreadPool().submit(() -> {
            client.onClientKickedFromChannel(channel, source, reason);
            server.removeChannel(channelName);
         });
      } else {
         // TODO Remove user from users list
         Client.getThreadPool().submit(() -> client.onUserKickedFromChannel(channel, source, reason));
      }
   }

   private void handleQuit(final Server server, final IrcPacket packet) {
      final Source source = packet.getPrefix() == null ? null : packet.getPrefixAsSource(server);
      if (source != null) {
         final String who = source.getName();
         final String reason = packet.getTrail();
         if (server.getClientNick().equals(who)) {
            server.setJoined(false);
            server.setConnected(false);
            Client.getThreadPool().submit(() -> client.onClientKickedFromServer(server, reason));
         } else {
            // TODO Remove user from users list (in all channels?)
            Client.getThreadPool().submit(() -> client.onUserQuitServer(server, reason));
         }
      }
   }

   private void handlePrivMsg(final Server server, final IrcPacket packet) {
      final Source source = packet.getPrefixAsSource(server);
      final String dest = packet.getParameters()[0];
      if (dest.startsWith("#")) {
         final boolean isBotCommand = client.getCommandManager() != null && client.getCommandManager().isCommand(packet.getTrail());
         final Channel channel = server.getChannel(dest);
         if (channel == null) {
            server.addChannel(dest);
         }
         if (isBotCommand) {
            Client.getThreadPool().submit(() -> client.getCommandManager().exec(server, channel, source, packet.getTrail(), false));
         } else {
            Client.getThreadPool().submit(() -> client.onChannelMessage(channel, source, packet.getTrail()));
         }
      } else {
         Client.getThreadPool().submit(() -> client.getCommandManager().exec(server, null, source, packet.getTrail(), true));
      }
   }
}
