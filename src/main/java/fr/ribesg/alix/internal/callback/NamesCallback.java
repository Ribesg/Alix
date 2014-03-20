package fr.ribesg.alix.internal.callback;
import fr.ribesg.alix.api.Channel;
import fr.ribesg.alix.api.callback.Callback;
import fr.ribesg.alix.api.enums.Codes;
import fr.ribesg.alix.api.enums.Reply;
import fr.ribesg.alix.api.message.IrcPacket;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents an internal Callback for the NAMES IRC Command.
 * <p>
 * The idea for this Callback is to handle the retrieval of Users of a
 * Channel properly.
 */
public class NamesCallback extends Callback {

	private static final Logger LOGGER = Logger.getLogger(NamesCallback.class.getName());

	private static final String[] LISTENED_CODES = new String[] {
			Reply.RPL_NAMREPLY.getIntCodeAsString(),
			Reply.RPL_ENDOFNAMES.getIntCodeAsString()
	};

	private final Channel     channel;
	private final Set<String> users;

	public NamesCallback(final Channel channel) {
		super(LISTENED_CODES);
		this.channel = channel;
		this.users = new HashSet<>();
	}

	public NamesCallback(final Channel channel, final Object lock) {
		super(lock, LISTENED_CODES);
		this.channel = channel;
		this.users = new HashSet<>();
	}

	@Override
	public boolean onIrcPacket(final IrcPacket packet) {
		String channelName;
		switch (Reply.getFromCode(packet.getRawCommandString())) {
			case RPL_NAMREPLY: // A part of the complete Users Set
				channelName = packet.getParameters()[2];
				if (this.channel.getName().equals(channelName)) {
					final String[] users = packet.getTrail().split(Codes.SP);
					Collections.addAll(this.users, users);
				}
				return false;
			case RPL_ENDOFNAMES: // Notification of the End of the Users Set
				channelName = packet.getParameters()[1];
				if (this.channel.getName().equals(channelName)) {
					channel.setUsers(this.users);
					unlock();
					return true;
				} else {
					return false;
				}
			default:
				throw new IllegalArgumentException(packet.toString());
		}
	}

	@Override
	public void onTimeout() {
		LOGGER.error("NAMES Command timed out! The users list of Channel " + this.channel.getName() + " has been emptied!");
		channel.clearUsers();
		unlock();
	}
}