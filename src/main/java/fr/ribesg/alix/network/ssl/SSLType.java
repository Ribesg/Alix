package fr.ribesg.alix.network.ssl;
/**
 * Represents a type of SSL handling.
 *
 * @author Ribesg
 */
public enum SSLType {
	/**
	 * No SSL at all
	 */
	NONE,

	/**
	 * Unsecured SSL connection, trusting any certificate
	 */
	TRUSTING,

	/**
	 * Secured SSL connection
	 */
	SECURED
}
