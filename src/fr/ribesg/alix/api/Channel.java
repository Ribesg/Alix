package fr.ribesg.alix.api;
/** @author Ribesg */
public interface Channel extends Receiver {

	/** @return The Server this channel belongs to. */
	public Server getServer();

	/**
	 * @return The name of this Channel on its Server with type char,
	 *         is unique per-Server
	 */
	public String getName();
}
