package fr.ribesg.alix.api;

import fr.ribesg.alix.api.enums.Command;

/** @author Ribesg */
public interface Server {

	/** @return the URL of this Server, could be an IP or a FQDN */
	public String getUrl();

	/**
	 * Sends a RAW message to this Receiver.
	 *
	 * @param command    The command to send
	 * @param parameters The parameters for this command
	 */
	public void sendRaw(Command command, String... parameters);

	/**
	 * Sends a RAW message to this Receiver, with a prefix.
	 * Will use {@link #sendRaw(fr.ribesg.alix.api.enums.Command, String...)}
	 * if prefix is null.
	 *
	 * @param prefix     The prefix of this message
	 * @param command    The command to send
	 * @param parameters The parameters for this command
	 */
	public void sendRaw(String prefix, Command command, String... parameters);
}
