package fr.ribesg.alix.api;
import fr.ribesg.alix.api.message.Message;

/**
 * Represents an IRC Client that can connect to IRC servers,
 * join channels on those various IRC servers, send messages,
 * actions and commands to those IRC servers/channels.
 *
 * @author Ribesg
 */
public interface Client {

	/**
	 * Gets the name of this Client.
	 *
	 * @return the name of this client
	 */
	public String getName();

	public void onServerJoined(final Server server);

	public void onChannelJoined(final Channel channel);

	public void onMessageInChannel(final Channel channel, final String message);

	public void onPrivateMessage(final String fromUser, final String message);

	public void onRawIrcMessage(final Server server, final Message message);

}
