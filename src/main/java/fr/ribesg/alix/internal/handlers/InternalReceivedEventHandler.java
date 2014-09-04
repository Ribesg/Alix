package fr.ribesg.alix.internal.handlers;

import fr.ribesg.alix.api.Channel;
import fr.ribesg.alix.api.Client;
import fr.ribesg.alix.api.EventManager;
import fr.ribesg.alix.api.Log;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.Source;
import fr.ribesg.alix.api.enums.Command;
import fr.ribesg.alix.api.enums.Reply;
import fr.ribesg.alix.api.event.*;
import fr.ribesg.alix.api.message.IrcPacket;
import fr.ribesg.alix.api.message.PongIrcPacket;

/**
 * TODO Javadoc
 * TODO Fix all TODOs, damn
 */
public class InternalReceivedEventHandler {

   private final Client client;

   public InternalReceivedEventHandler(final Client client) {
      this.client = client;
      EventManager.register(this);
   }

   @EventHandler(priority = EventHandlerPriority.INTERNAL, ignoreConsumed = true)
   public void onReceivedPacket(final ReceivedPacketEvent event) {
      final Server server = event.getSource();
      final IrcPacket packet = event.getPacket();
      server.setJoined(true);

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
                  Client.getThreadPool().submit(() -> EventManager.call(new ServerJoinEvent(server)));
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
   }

   private void handleJoinPart(final Server server, final boolean isJoin, final IrcPacket packet) {
      // Workaround for IRCds using the trail as parameter (Unreal)
      final String channelName = packet.getParameters().length > 0 ? packet.getParameters()[0] : packet.getTrail();
      Channel channel = server.getChannel(channelName);
      if (channel == null) {
         channel = server.addChannel(channelName);
      } else {
         channel.setName(channelName);
      }
      channel.setJoined(true);
      final Channel finalChannel = channel;
      final Source source = packet.getPrefix() == null ? null : packet.getPrefixAsSource(server);
      if (source == null || source.getName().equals(server.getClientNick())) {
         if (isJoin) {
            Client.getThreadPool().submit(() -> EventManager.call(new ClientJoinChannelEvent(finalChannel)));
         } else {
            Client.getThreadPool().submit(() -> EventManager.call(new ClientPartChannelEvent(finalChannel)));
         }
      } else {
         if (isJoin) {
            // TODO Fetch info about user (+, @) and add it to the users list
            Client.getThreadPool().submit(() -> EventManager.call(new UserJoinChannelEvent(source, finalChannel)));
         } else {
            // TODO Remove user from users list
            Client.getThreadPool().submit(() -> EventManager.call(new UserPartChannelEvent(source, finalChannel)));
         }
      }
   }

   private void handleKick(final Server server, final IrcPacket packet) {
      final String channelName = packet.getParameters()[0];
      final String who = packet.getParameters()[1];
      final Channel channel = server.getChannel(channelName);
      channel.setJoined(true);
      final Source source = packet.getPrefix() == null ? null : packet.getPrefixAsSource(server);
      final String reason = packet.getTrail();
      if (server.getClientNick().equals(who)) {
         Client.getThreadPool().submit(() -> EventManager.call(new ClientKickedFromChannelEvent(channel, source, reason)));
      } else {
         // TODO Remove user from users list
         Client.getThreadPool().submit(() -> EventManager.call(new UserKickedFromChannelEvent(channel, source, reason)));
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
            Client.getThreadPool().submit(() -> EventManager.call(new ClientQuitServerEvent(server, reason)));
         } else {
            // TODO Remove user from users list (in all channels?)
            Client.getThreadPool().submit(() -> EventManager.call(new UserQuitServerEvent(server, source, reason)));
         }
      }
   }

   private void handlePrivMsg(final Server server, final IrcPacket packet) {
      final Source source = packet.getPrefixAsSource(server);
      final String dest = packet.getParameters()[0];
      if (dest.startsWith("#")) {
         Channel channel = server.getChannel(dest);
         if (channel == null) {
            channel = server.addChannel(dest);
         }
         channel.setJoined(true);
         final Channel finalChannel = channel;
         Client.getThreadPool().submit(() -> EventManager.call(new ChannelMessageEvent(finalChannel, source, packet.getTrail())));
      } else {
         Client.getThreadPool().submit(() -> EventManager.call(new PrivateMessageEvent(server, source, packet.getTrail())));
      }
   }
}
