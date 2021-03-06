/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.api.bot.command;

import fr.ribesg.alix.api.Channel;
import fr.ribesg.alix.api.EventManager;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.Source;
import fr.ribesg.alix.api.event.ChannelMessageEvent;
import fr.ribesg.alix.api.event.EventHandler;
import fr.ribesg.alix.api.event.EventHandlerPriority;
import fr.ribesg.alix.api.event.PrivateMessageEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a Command Manager.
 */
public class CommandManager {

   /**
    * A Map of Command names to Commands
    */
   final Map<String, Command> commands;

   /**
    * A Map of Command aliases to Command names
    */
   final Map<String, String> aliases;

   /**
    * Admins of this Bot
    */
   private final Set<String> botAdmins;

   /**
    * Required prefix for a Message to be considered as a Command call
    */
   private final String commandPrefix;

   /**
    * Message sent to the user when the provided command isn't known
    */
   private String unknownCommandMessage = "I don't know the Command '%s'!";

   /**
    * Message sent to the user when the provided command is restricted and
    * he doesn't have the right to use it
    */
   private String forbiddenMessage = "You can't use the command '%s'!";

   /**
    * CommandManager constructor.
    *
    * @param commandPrefix the required prefix for a Message to be
    *                      considered as a Command call
    * @param botAdmins     a Set of Nicknames that has to be considered as Bot
    *                      admins
    *
    * @throws IllegalArgumentException if the commandPrefix is invalid
    */
   public CommandManager(final String commandPrefix, final Set<String> botAdmins) {
      if (!commandPrefix.matches("^[^\\s\\t\\r\\n]+$")) {
         throw new IllegalArgumentException("Invalid command prefix: " + commandPrefix);
      }

      this.commands = new HashMap<>();
      this.aliases = new HashMap<>();
      this.botAdmins = botAdmins;
      this.commandPrefix = commandPrefix;

      EventManager.register(this);

      this.registerCommand(new HelpCommand(this));
   }

   @EventHandler(priority = EventHandlerPriority.INTERNAL)
   public void onChannelMessage(final ChannelMessageEvent event) {
      if (this.isCommand(event.getMessage())) {
         if (this.exec(event.getChannel().getServer(), event.getChannel(), event.getUser(), event.getMessage(), false)) {
            event.consume();
         }
      }
   }

   @EventHandler(priority = EventHandlerPriority.INTERNAL)
   public void onPrivateMessage(final PrivateMessageEvent event) {
      if (this.exec(event.getServer(), null, event.getFrom(), event.getMessage(), true)) {
         event.consume();
      }
   }

   /**
    * Registers a Command.
    * This checks for collisions.
    *
    * @param command the Command to register
    *
    * @throws IllegalArgumentException if there is a Collision with another
    *                                  Command name/alias
    */
   public void registerCommand(final Command command) {
      final String name = command.getName();
      final String[] aliases = command.getAliases();

      // Check for collisions
      if (this.commands.containsKey(name)) {
         throw new IllegalArgumentException("Failed to register Command '" + name + "': a Command with name '" + name + "' already exists!");
      } else if (this.aliases.containsKey(name)) {
         throw new IllegalArgumentException("Failed to register Command '" + name + "': the command '" + this.aliases.get(name) + "' already has '" + name + "' as an alias!");
      } else {
         for (final String alias : aliases) {
            if (this.commands.containsKey(alias)) {
               throw new IllegalArgumentException("Failed to register Command '" + name + "': a Command with name '" + alias + "' already exists!");
            } else if (this.aliases.containsKey(alias)) {
               throw new IllegalArgumentException("Failed to register Command '" + name + "': the command '" + this.aliases.get(alias) + "' already has '" + alias + "' as an alias!");
            }
         }
      }

      // Add the Command
      this.commands.put(name, command);
      for (final String alias : aliases) {
         this.aliases.put(alias, name);
      }
   }

   /**
    * Checks if a Message could be a Command.
    *
    * @param message the message to check
    *
    * @return true if this message starts with the commandPrefix followed
    * by a String, false otherwise.
    */
   public boolean isCommand(final String message) {
      return message.startsWith(this.commandPrefix) && !message.startsWith(this.commandPrefix + ' ');
   }

   /**
    * Executes a message as a Command. The return value decides if the
    * related message event is consumed or not.
    *
    * @param server         the Server the message was sent to
    * @param channel        the Channel the message was sent to, or null if it's
    *                       a private message
    * @param user           the user who sent the message
    * @param message        the message sent
    * @param privateMessage if this was sent as a private message
    *
    * @return true if the input was a valid command, false otherwise
    *
    * @throws IllegalArgumentException if the provided message doesn't start
    *                                  with a Command call
    */
   public boolean exec(final Server server, final Channel channel, final Source user, final String message, final boolean privateMessage) {
      if (!privateMessage && !isCommand(message)) {
         throw new IllegalArgumentException("Provided message is not a Command, please use isCommand(...) before calling exec(...)");
      }

      // Get the provided command name
      final String[] messageSplit = message.split("\\s");
      final String cmdAndPrimaryArgument;
      if (messageSplit[0].startsWith(this.commandPrefix)) {
         cmdAndPrimaryArgument = messageSplit[0].substring(this.commandPrefix.length()).toLowerCase();
      } else {
         cmdAndPrimaryArgument = messageSplit[0];
      }

      final String cmd;
      final String primaryArgument;
      if (cmdAndPrimaryArgument.contains(".")) {
         final int dotLocation = cmdAndPrimaryArgument.indexOf('.');
         cmd = cmdAndPrimaryArgument.substring(0, dotLocation);
         primaryArgument = cmdAndPrimaryArgument.substring(dotLocation + 1, cmdAndPrimaryArgument.length());
      } else {
         cmd = cmdAndPrimaryArgument;
         primaryArgument = null;
      }

      // Find the Command
      Command command = this.commands.get(cmd);
      if (command == null) {
         final String realCmd = this.aliases.get(cmd);
         if (realCmd == null) {
            // Unknown Command
            if (this.unknownCommandMessage != null && !this.unknownCommandMessage.isEmpty()) {
               final String formattedMessage = String.format(this.unknownCommandMessage, cmd);
               if (channel == null) {
                  user.sendMessage(formattedMessage);
               } else {
                  channel.sendMessage(user.getName() + ", " + formattedMessage);
               }
            }
            return false;
         } else {
            command = this.commands.get(realCmd);
         }
      }

      // Check for rights
      // TODO Check for NickServ registration
      if (command.isRestricted() && !botAdmins.contains(user.getName()) && !command.getAllowedNickNames().contains(user.getName())) {
         if (this.forbiddenMessage != null && !this.forbiddenMessage.isEmpty()) {
            final String formattedMessage = String.format(this.forbiddenMessage, cmd);
            if (channel == null) {
               user.sendMessage(formattedMessage);
            } else {
               channel.sendMessage(user.getName() + ", " + formattedMessage);
            }
         }
         return true;
      }

      // Get args
      final String[] args = Arrays.copyOfRange(messageSplit, 1, messageSplit.length);

      // Execute the Command
      if (!command.exec(server, channel, user, primaryArgument, args)) {
         command.sendUsage(this.commandPrefix, channel == null ? user : channel);
      }
      return true;
   }

   /**
    * @return the admins of this Bot
    */
   public Set<String> getBotAdmins() {
      return this.botAdmins;
   }

   /**
    * @return the prefix used for this CommandManager
    */
   public String getCommandPrefix() {
      return commandPrefix;
   }

   /**
    * @return the message sent to the user when the provided command isn't
    * known
    */
   public String getUnknownCommandMessage() {
      return this.unknownCommandMessage;
   }

   /**
    * Sets the message sent to the user when the provided command isn't
    * known. This can be set to null or empty to prevent sending a
    * message for unknown Commands.
    *
    * @param unknownCommandMessage the new message sent to the user when
    *                              the provided Command isn't known, or
    *                              null or empty if you do not want a
    *                              message to be sent in this case
    */
   public void setUnknownCommandMessage(final String unknownCommandMessage) {
      this.unknownCommandMessage = unknownCommandMessage;
   }

   /**
    * @return the message sent to the user when the provided command is
    * restricted and he doesn't have the right to use it
    */
   public String getForbiddenMessage() {
      return this.forbiddenMessage;
   }

   /**
    * Sets the message sent to the user when the provided command is
    * restricted and he doesn't have the right to use it.
    * This can be set to null or empty to prevent sending a
    * message for forbidden Commands.
    *
    * @param forbiddenMessage the new message sent to the user when
    *                         the provided Command is restricted and he
    *                         doesn't have the right to use it, or
    *                         null or empty if you do not want a
    *                         message to be sent in this case
    */
   public void setForbiddenMessage(final String forbiddenMessage) {
      this.forbiddenMessage = forbiddenMessage;
   }
}
