package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.Source;
import fr.ribesg.alix.api.enums.Codes;
import fr.ribesg.alix.api.enums.Command;
import fr.ribesg.alix.api.enums.Reply;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an IRC Message.
 * <p/>
 * An IRC Message may contain a prefix followed by a command and up to
 * 15 parameters.
 * Here is an example IRC Message:
 * <p/>
 * <strong>:PREFIX COMMAND PARAM1 PARAM2 PARAM3 :TRAIL</strong>
 * <p/>
 * Notes:
 * <ul>
 * <li>The message starts with a ':' followed by the prefix, which can be empty.
 * <li>All elements are separated by a space character, and the message is
 * ended by a CRLF.
 * <li>Only the COMMAND part is mandatory.
 * <li>If the TRAIL part it missing, then the : is not present
 * </ul>
 *
 * @author Ribesg
 */
public class Message {

	private static final Pattern IRC_MESSAGE_REGEX = Pattern.compile("^(?:[:](?<prefix>\\S+) )?(?<command>\\S+)(?: (?!:)(?<params>.+?))?(?: [:](?<trail>.+))?$");

	private static final Pattern PREFIX_REGEX = Pattern.compile("^(?<name>[^\\s!@]+)(?:!(?<userName>[^\\s@]+))?(?:@(?<hostName>\\S+))?$");

	private static final Pattern SERVER_NAME_REGEX = Pattern.compile("^(?:\\w+\\.)+(?:\\w+)$");

	/**
	 * Parse a Message object from a String.
	 *
	 * @param stringMessage the String to parse
	 *
	 * @return a Message object
	 */
	public static Message parseMessage(final String stringMessage) {
		final Matcher matcher = IRC_MESSAGE_REGEX.matcher(stringMessage);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Malformed IRC Message: '" + stringMessage + "'. Please report this so it can get fixed!");
		} else {
			final String prefix = matcher.group("prefix");
			final String command = matcher.group("command");
			final String paramsString = matcher.group("params");
			final String trail = matcher.group("trail");
			final String[] params = paramsString == null ? new String[0] : paramsString.replaceAll("\\s+", Codes.SP.toString()).split(Codes.SP.toString());
			return new Message(prefix, command, trail, params);
		}
	}

	/**
	 * Parse the provided prefix as a Source.
	 *
	 * @param server the Server linked to this prefix, required to build
	 *               the Source object
	 * @param prefix the prefix to parse
	 *
	 * @return the Source related to this prefix
	 *
	 * @throws NullPointerException     if prefix is null
	 * @throws IllegalArgumentException if it fails to parse the prefix
	 */
	public static Source parsePrefix(final Server server, final String prefix) {
		if (prefix == null) {
			throw new NullPointerException("The provided prefix is null. Please check before calling this.");
		} else {
			final Matcher matcher = PREFIX_REGEX.matcher(prefix);
			if (matcher.matches()) {
				final String name = matcher.group("name");
				final String userName = matcher.group("userName");
				final String hostName = matcher.group("hostName");

				if (SERVER_NAME_REGEX.matcher(name).matches()) {
					return new Source(server, name);
				} else {
					return new Source(server, name, userName, hostName);
				}
			} else {
				throw new IllegalArgumentException("Failed to parse '" + prefix + "' as a prefix. Please report this.");
			}
		}
	}

	private       String   prefix;
	private final String   command;
	private       String[] parameters;
	private       String   trail;

	/**
	 * Minimal constructor
	 *
	 * @param command the command of this Message
	 */
	public Message(final String command) {
		this.prefix = null;
		this.command = command;
		this.parameters = null;
		this.trail = null;
	}

	/**
	 * Complete constructor
	 *
	 * @param prefix     the prefix of this Message
	 * @param command    the command of this Message
	 * @param trail      the trail of this Message
	 * @param parameters the parameters of this Message
	 */
	public Message(final String prefix, final String command, final String trail, final String... parameters) {
		this.prefix = prefix;
		this.command = command;
		this.parameters = parameters;
		this.trail = trail;
	}

	// ################### //
	// ## Stock Getters ## //
	// ################### //

	/**
	 * Gets the raw Message String in the following format:
	 * <p/>
	 * <strong>:PREFIX COMMAND PARAM1 PARAM2 PARAM3 :TRAIL\n</strong>
	 * <p/>
	 *
	 * @return the raw Message String of this Message
	 */
	public String getRawMessage() {
		final StringBuilder result = new StringBuilder(Codes.COLON.toString());
		if (this.prefix != null) {
			result.append(this.prefix);
		}
		result.append(Codes.SP);
		result.append(this.command);
		if (getParameters() != null) {
			for (final String param : this.parameters) {
				result.append(Codes.SP).append(param);
			}
		}
		if (this.trail != null && this.trail.length() > 0) {
			result.append(Codes.SP);
			result.append(Codes.COLON);
			result.append(this.trail);
		}
		return result.append(Codes.CRLF).toString();
	}

	/**
	 * Gets the prefix of this Message. May be null if this Message has no
	 * prefix.
	 *
	 * @return the prefix of this Message or null if not present
	 */
	public String getPrefix() {
		return this.prefix;
	}

	/**
	 * Gets the raw Command part of this Message.
	 * It may be an IRC Command or an IRC Reply code.
	 *
	 * @return the command of this Message
	 */
	public String getRawCommandString() {
		return this.command;
	}

	/**
	 * Gets the parameters of this Message. This array should not contain
	 * more than 15 elements, but the API is not secured on this side, so
	 * don't rely on it being in the 0-15 range.
	 *
	 * @return the parameters of this Message
	 */
	public String[] getParameters() {
		return this.parameters;
	}

	/**
	 * Gets the trail, which is the last parameter of the message.
	 * This parameter is the only one that can contain spaces. For example,
	 * it is this parameter which contains the actual message in a PRIVMSG.
	 *
	 * @return the trail of this Message
	 */
	public String getTrail() {
		return this.trail;
	}

	// ################################### //
	// ## Working Getters & other stuff ## //
	// ################################### //

	/**
	 * Parse the prefix of this Message as a Source.
	 *
	 * @param server the Server linked to this Message, required to build
	 *               the Source object
	 *
	 * @return the Source related to this Message's prefix
	 *
	 * @throws IllegalStateException    if called on a Message without prefix
	 * @throws IllegalArgumentException if it fails to parse the prefix
	 * @see #parsePrefix(fr.ribesg.alix.api.Server, String)
	 */
	public Source getPrefixAsSource(final Server server) {
		try {
			return parsePrefix(server, this.prefix);
		} catch (final NullPointerException e) {
			throw new IllegalStateException("This Message's prefix is null. Please check before calling this.", e);
		}
	}

	/**
	 * Checks if the Command part of this Message is a
	 * valid IRC Command.
	 *
	 * @return true if the Command part of this Message is a valid Command,
	 * false otherwise
	 */
	public boolean isValidCommand() {
		try {
			Command.valueOf(this.command);
			return true;
		} catch (final IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * Gets this Message's Command part as a Command Enum value.
	 *
	 * @return this Message's Command Enum value
	 *
	 * @throws IllegalStateException if the Command part of this
	 *                               Message is not a valid Command
	 *                               Enum value.
	 */
	public Command getCommandAsCommand() {
		if (!isValidCommand()) {
			throw new IllegalStateException("Not a valid Command!");
		} else {
			return Command.valueOf(this.command);
		}
	}

	/**
	 * Checks if the Command part of this Message is a
	 * valid IRC Reply code.
	 *
	 * @return true if the Command part of this Message is a valid Reply
	 * code, false otherwise
	 */
	public boolean isValidReply() {
		return Reply.getFromCode(this.command) != null;
	}

	/**
	 * Gets this Message's Command part as a Reply Enum value.
	 *
	 * @return this Message's Command Reply Enum value
	 *
	 * @throws IllegalStateException if the Command part of this
	 *                               Message is not a valid Reply
	 *                               Enum value.
	 */
	public Reply getCommandAsReply() {
		if (!isValidReply()) {
			throw new IllegalStateException("Not a valid Reply code!");
		} else {
			return Reply.getFromCode(this.command);
		}
	}

	/**
	 * Convenient method.
	 *
	 * @return the raw Message String of this Message
	 */
	@Override
	public String toString() {
		return this.getRawMessage();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Message)) {
			return false;
		}

		Message message = (Message) o;

		if (!command.equals(message.command)) {
			return false;
		}
		if (!Arrays.equals(parameters, message.parameters)) {
			return false;
		}
		if (prefix != null ? !prefix.equals(message.prefix) : message.prefix != null) {
			return false;
		}
		if (trail != null ? !trail.equals(message.trail) : message.trail != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = prefix != null ? prefix.hashCode() : 0;
		result = 31 * result + command.hashCode();
		result = 31 * result + (parameters != null ? Arrays.hashCode(parameters) : 0);
		result = 31 * result + (trail != null ? trail.hashCode() : 0);
		return result;
	}
}
