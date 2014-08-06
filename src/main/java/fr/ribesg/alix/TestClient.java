/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix;
import fr.ribesg.alix.api.Channel;
import fr.ribesg.alix.api.Client;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.Source;
import fr.ribesg.alix.api.bot.command.Command;
import fr.ribesg.alix.api.bot.command.CommandManager;
import fr.ribesg.alix.api.event.ChannelMessageEvent;
import fr.ribesg.alix.api.event.ClientJoinChannelEvent;
import fr.ribesg.alix.api.event.EventHandler;
import fr.ribesg.alix.api.event.PrivateMessageEvent;
import fr.ribesg.alix.api.event.ServerJoinEvent;
import fr.ribesg.alix.api.event.UserJoinChannelEvent;
import fr.ribesg.alix.api.message.PrivMsgIrcPacket;

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
         protected boolean load() {
            final Server server = new Server(this, "EsperNet", "irc.esper.net", 6667);
            server.addChannel("#alix");
            this.getServers().add(server);

            this.createCommandManager("!", null);

            final CommandManager manager = this.getCommandManager();
            manager.registerCommand(new Command("test", new String[] {"## - Just a test command"}) {

               @Override
               public boolean exec(final Server server, final Channel channel, final Source user, final String primaryArgument, final String[] args) {
                  if (channel == null) {
                     user.sendMessage("Use the !test command in a Channel!");
                  } else {
                     channel.sendMessage("So " +
                                         user.getName() +
                                         " used the command " +
                                         this.getName() +
                                         " in the Channel " +
                                         channel.getName() +
                                         "!");
                  }
                  return true;
               }
            });
            return true;
         }

         @EventHandler
         public void onServerJoined(final ServerJoinEvent event) {
            /* Here you can register with NickServ for example
            final Server server = event.getServer();
            server.send(new PrivMsgIrcPacket("NickServ", "REGISTER SomePassword some@email"));
				server.send(new PrivMsgIrcPacket("NickServ", "IDENTIFY SomePassword"));
				*/
         }

         @EventHandler
         public void onClientJoinChannel(final ClientJoinChannelEvent event) {
            event.getChannel().sendMessage("Hi!");
         }

         @EventHandler
         public void onUserJoinChannel(final UserJoinChannelEvent event) {
            event.getChannel().sendMessage(event.getUser().getName() + ", Hi!");
         }

         @EventHandler
         public void onPrivateMessage(final PrivateMessageEvent event) {
            final Server server = event.getServer();
            final Source fromSource = event.getFrom();
            final String message = event.getMessage();
            server.send(new PrivMsgIrcPacket(fromSource.getName(), "Hi!"));
            if (message.equalsIgnoreCase(server.getClientNick() + ", quit")) {
               // Disconnect from server
               server.disconnect();
            } else if (message.startsWith("!pm ") && message.length() > "!pm ".length() + 1) {
               // Simple command to ask to SomeBot to send a pm to someone
               final String dest = message.split(" ")[1];
               final String mes = message.substring("!pm ".length() + dest.length() + 1);
               server.send(new PrivMsgIrcPacket(dest, mes));
            }
         }

         @EventHandler
         public void onChannelMessage(final ChannelMessageEvent event) {
            final String message = event.getMessage();
            final Channel channel = event.getChannel();
            final Source fromSource = event.getUser();
            if (message.equalsIgnoreCase(channel.getServer().getClientNick() + ", quit")) {
               // Disconnect from server
               channel.getServer().disconnect();
            } else if (message.startsWith(channel.getServer().getClientNick() + ", ")) {
               // Repeat message
               channel.sendMessage(fromSource.getName() + message.substring(channel.getServer().getClientNick().length()));
            }
         }
      };
   }

}
