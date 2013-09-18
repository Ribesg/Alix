package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Codes;

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
	 * Gets the raw command part of this Message.
	 *
	 * @return the command of this Message
	 */
	public String getRawCommand() {
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
			result.append(Codes.COLON);
			result.append(this.trail);
		}
		return result.append(Codes.CRLF).toString();
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
}
