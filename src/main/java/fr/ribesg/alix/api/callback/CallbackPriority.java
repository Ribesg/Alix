package fr.ribesg.alix.api.callback;
/**
 * This enum represents Callback priorities relative to each other and
 * also relative to the internal IRC Packet handler.
 * <p>
 * The default priority for Callbacks is {@link #LOW}.
 *
 * @author Ribesg
 */
public enum CallbackPriority {

	/**
	 * Called before {@link #HIGH} Priority Callbacks
	 */
	HIGHEST,

	/**
	 * Called <em>before</em> internal packet handling
	 */
	HIGH,

	/**
	 * Called <em>after</em> internal packet handling
	 */
	LOW,

	/**
	 * Called after {@link #LOW} Priority Callbacks
	 */
	LOWEST
}
