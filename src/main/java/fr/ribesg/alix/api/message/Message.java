package fr.ribesg.alix.api.message;
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
public interface Message {

	/**
	 * Gets the prefix of this Message. May be null if this Message has no
	 * prefix.
	 *
	 * @return the prefix of this Message or null if not present
	 */
	public String getPrefix();

	/**
	 * Gets the raw command part of this Message.
	 *
	 * @return the command of this Message
	 */
	public String getRawCommand();

	/**
	 * Gets the parameters of this Message. This array should not contain
	 * more than 15 elements, but the API is not secured on this side, so
	 * don't rely on it being in the 0-15 range.
	 *
	 * @return the parameters of this Message
	 */
	public String[] getParameters();

	/**
	 * Gets the trail, which is the last parameter of the message.
	 * This parameter is the only one that can contain spaces. For example,
	 * it is this parameter which contains the actual message in a PRIVMSG.
	 *
	 * @return the trail of this Message
	 */
	public String getTrail();

	/**
	 * Gets the raw Message String in the following format:
	 * <p/>
	 * <strong>:PREFIX COMMAND PARAM1 PARAM2 PARAM3 :TRAIL</strong>
	 * <p/>
	 *
	 * @return the raw Message String of this Message
	 */
	public String getRawMessage();
}
