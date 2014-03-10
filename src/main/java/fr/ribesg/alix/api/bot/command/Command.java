package fr.ribesg.alix.api.bot.command;

import fr.ribesg.alix.api.Channel;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.Source;

import java.util.Set;

/**
 * Represents a Command
 */
public abstract class Command {

	/**
	 * The CommandManager this Command belongs to.
	 */
	private final CommandManager manager;

	/**
	 * The name of this Command, the main String that has to be written for
	 * the command to be used, without prefix.
	 */
	private final String name;

	/**
	 * Aliases of this Command, some Strings you can use instead of this
	 * Command's name.
	 */
	private final String[] aliases;

	/**
	 * If this Command is a restricted Command or not.
	 * A restricted Command cannot be ran by everybody, the nickname has to be
	 * a bot admin or in the {@link #allowedNickNames} Set.
	 */
	private final boolean restricted;

	/**
	 * Set of Nicknames allowed to use this Command.
	 * Nicknames have to be registered and identified with NickServ for this
	 * to be effective.
	 * This is not considered if {@link #restricted} is false.
	 */
	private final Set<String> allowedNickNames;

	/**
	 * Public Command constructor.
	 * Calls {@link #Command(CommandManager, String, boolean, Set, String...)}.
	 *
	 * @param manager the CommandManager this Command belongs to
	 * @param name    the name of this Command
	 *
	 * @see #Command(CommandManager, String, boolean, Set, String...) for non-public Command
	 */
	public Command(final CommandManager manager, final String name) {
		this(manager, name, false, null);
	}

	/**
	 * Public Command with aliases constructor.
	 * Calls {@link #Command(CommandManager, String, boolean, Set, String...)}.
	 *
	 * @param manager the CommandManager this Command belongs to
	 * @param name    the name of this Command
	 * @param aliases possible aliases for this Command
	 *
	 * @see #Command(CommandManager, String, boolean, Set, String...) for non-public Command
	 */
	public Command(final CommandManager manager, final String name, final String... aliases) {
		this(manager, name, false, null, aliases);
	}

	/**
	 * Complete Command constructor.
	 * Should be used for restricted Commands.
	 *
	 * @param manager          the CommandManager this Command belongs to
	 * @param name             the name of this Command
	 * @param restricted       if this Command is restricted or public
	 * @param allowedNickNames a Set of allowed nicknames, all registered
	 *                         with the NickServ Service
	 * @param aliases          possible aliases for this Command
	 *
	 * @throws IllegalArgumentException if the Command is public and a Set
	 *                                  of allowedNickNames was provided
	 */
	public Command(final CommandManager manager,
	               final String name,
	               final boolean restricted,
	               final Set<String> allowedNickNames,
	               final String... aliases) {
		if (!restricted && allowedNickNames != null) {
			throw new IllegalArgumentException("A public Command should not have allowedNickNames, did you do something wrong?");
		}
		this.manager = manager;
		this.name = name.toLowerCase();
		this.aliases = aliases;
		this.restricted = restricted;
		this.allowedNickNames = allowedNickNames;

		// Make the aliases lowercase, too
		for (int i = 0; i < this.aliases.length; i++) {
			this.aliases[i] = this.aliases[i].toLowerCase();
		}
	}

	/**
	 * Gets the name of this Command, the main String that has to be written
	 * for the command to be used, without prefix.
	 *
	 * @return the name of this Command
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the aliases of this Command, some Strings you can use instead of
	 * this Command's name.
	 *
	 * @return possible aliases for this Command, may be of length 0
	 */
	public String[] getAliases() {
		return aliases;
	}

	/**
	 * Checks if this Command is a restricted Command or not.
	 * A restricted Command cannot be ran by everybody, the nickname has to be
	 * a bot admin or in the {@link #getAllowedNickNames()} Set.
	 *
	 * @return true if this Command is restricted, false otherwise
	 */
	public boolean isRestricted() {
		return restricted;
	}

	/**
	 * Gets a Set of Nicknames allowed to use this Command.
	 * Nicknames have to be registered and identified with the NickServ
	 * Service for this to be effective.
	 * This should not be considered if {@link #isRestricted()} is false.
	 *
	 * @return a Set of allowed Nicknames if this Command is restricted,
	 * null otherwise
	 */
	public Set<String> getAllowedNickNames() {
		return allowedNickNames;
	}

	/**
	 * Executes this Command.
	 *
	 * @param server  the Server this Command has been called from
	 * @param channel the Channel this Command has been called in, or
	 *                null if there's none (i.e. if it's a private message)
	 * @param user    the User that wrote the Command
	 * @param args    arguments passed the the Command
	 */
	public abstract void exec(final Server server, final Channel channel, final Source user, final String[] args);

	public String toString() {
		return this.manager.getCommandPrefix() + getName();
	}
}
