package fr.ribesg.alix.api;
/**
 * Represents an IRC Message.
 * <p/>
 * An IRC Message may contain a prefix followed by a command and up to
 * 15 parameters.
 * Here is an example IRC Message:
 * <p/>
 * <strong>:PREFIX COMMAND PARAM1 PARAM2 PARAM3</strong>
 * <p/>
 * The message starts with a ':' followed by the prefix, which can be empty.
 * All elements are separated by a space character, and the message is
 * ended by a CRLF.
 *
 * @author Ribesg
 */
public interface Message {

	/** @return The Prefix of this Message. May be empty. */
	public String getPrefix();

	/** @return The Command of this Message. */
	public String getCommand();

	/**
	 * @return The Parameters of this Message. This array should contain more
	 *         than 15 elements, but the API is not secured on this side, so
	 *         don't rely on it being in the 0-15 range.
	 */
	public String[] getParameters();

	/**
	 * @return The Raw Message in the following format:
	 *         :[PREFIX] COMMAND [PARAMETER...]
	 */
	public String getRawMessage();
}
