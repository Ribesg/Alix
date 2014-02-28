package fr.ribesg.alix.api.bot.commands;
import fr.ribesg.alix.api.Channel;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.Source;

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
	private final Map<String, Command> commands;

	/**
	 * A Map of Command aliases to Command names
	 */
	private final Map<String, String> aliases;

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
	 * Executes a message as a Command.
	 *
	 * @param server  the Server the message was sent to
	 * @param channel the Channel the message was sent to, or null if it's
	 *                a private message
	 * @param user    the user who sent the message
	 * @param message the message sent
	 *
	 * @throws IllegalArgumentException if the provided message doesn't start
	 *                                  with a Command call
	 */
	public void exec(final Server server, final Channel channel, final Source user, final String message) {
		if (!isCommand(message)) {
			throw new IllegalArgumentException("Provided message is not a Command, please use isCommand(...) before calling exec(...)");
		}

		// Get the provided command name
		final String cmd = message.split("\\s")[0].substring(this.commandPrefix.length()).toLowerCase();

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
				return;
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
			return;
		}

		// Execute the Command
		command.exec(server, channel, user);
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
