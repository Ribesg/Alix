package fr.ribesg.alix.api.callback;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.message.IrcPacket;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a Callback for a sent IrcPacket.
 */
public abstract class Callback {

	private static final Logger       LOGGER = Logger.getLogger(Callback.class.getName());
	private static final NumberFormat format = new DecimalFormat("#.##");

	/**
	 * Default Timeout, in milliseconds: 30 seconds
	 */
	protected static final long DEFAULT_TIMEOUT = 30 * 1_000;

	/**
	 * Set of listened Commands and Reply codes
	 */
	protected final Set<String> listenedCodes;

	/**
	 * Time after which this Callback should call {@link #onTimeout} and be
	 * destroyed.
	 */
	protected final long timeoutDuration;

	/**
	 * Date at which this Callback should call {@link #onTimeout} and be
	 * destroyed.
	 */
	protected final long timeoutDate;

	/**
	 * The Original IRC Packet for which the Callback was set up.
	 * <p/>
	 * Should be set by the send-like methods to be more user-friendly than
	 * the User having to repeat the original IRC Packet twice in the method
	 * call.
	 */
	protected IrcPacket originalIrcPacket;

	/**
	 * The Server the Original IRC Packet was sent to.
	 * <p/>
	 * Should be set by the send-like methods to be more user-friendly than
	 * the User having to repeat the Server twice in the method
	 * call.
	 */
	protected Server server;

	/**
	 * Main Callback constructor.
	 * <p/>
	 * Pass some {@link fr.ribesg.alix.api.enums.Command} and/or some
	 * {@link fr.ribesg.alix.api.enums.Reply} codes to it to restrict
	 * calls to {@link #onIrcPacket(IrcPacket)} to them.
	 * <p/>
	 * If no argument is passed, {@link #onIrcPacket(IrcPacket)} will be
	 * called for every incoming {@link IrcPacket} until the method
	 * returns true.
	 * <p/>
	 * Of course listened Codes have to be uppercase to follow IRC RFCs.
	 *
	 * @param timeoutDuration the time after which this Callback should call
	 *                        {@link #onTimeout} and be destroyed, in
	 *                        milliseconds
	 * @param listenedCodes   listened Commands and Reply codes, can be empty
	 *                        to listen to everything
	 */
	public Callback(final long timeoutDuration, final String... listenedCodes) {
		this.timeoutDuration = timeoutDuration;
		this.timeoutDate = System.currentTimeMillis() + timeoutDuration;
		if (listenedCodes.length != 0) {
			this.listenedCodes = new HashSet<>();
			Collections.addAll(this.listenedCodes, listenedCodes);
		} else {
			this.listenedCodes = null;
		}
	}

	/**
	 * Callback constructor with default timeout of 30 seconds.
	 * <p/>
	 * Pass some {@link fr.ribesg.alix.api.enums.Command} and/or some
	 * {@link fr.ribesg.alix.api.enums.Reply} codes to it to restrict
	 * calls to {@link #onIrcPacket(IrcPacket)} to them.
	 * <p/>
	 * If no argument is passed, {@link #onIrcPacket(IrcPacket)} will be
	 * called for every incoming {@link IrcPacket} until the method
	 * returns true.
	 * <p/>
	 * Of course listened Codes have to be uppercase to follow IRC RFCs.
	 *
	 * @param listenedCodes listened Commands and Reply codes, can be empty
	 *                      to listen to everything
	 */
	public Callback(final String... listenedCodes) {
		this(DEFAULT_TIMEOUT, listenedCodes);
	}

	/**
	 * @return the listened Commands and Reply codes, or null if this
	 * Callback listens to all codes
	 */
	public Set<String> getListenedCodes() {
		return listenedCodes;
	}

	/**
	 * @return the time after which this Callback should call
	 * {@link #onTimeout} and be destroyed.
	 */
	public long getTimeoutDuration() {
		return timeoutDuration;
	}

	/**
	 * @return the date at which this Callback should call {@link #onTimeout}
	 * and be destroyed.
	 */
	public long getTimeoutDate() {
		return timeoutDate;
	}

	/**
	 * @return the Original IRC Packet for which the Callback was set up
	 */
	public IrcPacket getOriginalIrcPacket() {
		return originalIrcPacket;
	}

	/**
	 * You should not call this.
	 * <p/>
	 * This should only be called by the Server's send-like methods to link
	 * the original IrcPacket to its Callback, for the user not to have to
	 * do it.
	 */
	public void setOriginalIrcPacket(final IrcPacket originalIrcPacket) {
		this.originalIrcPacket = originalIrcPacket;
	}

	/**
	 * You should not call this.
	 * <p/>
	 * This should only be called by the Server's send-like methods to link
	 * the original IrcPacket to its Callback, for the user not to have to
	 * do it.
	 */
	public void setServer(final Server server) {
		this.server = server;
	}

	/**
	 * Check that the provided code is listened by this Callback
	 *
	 * @param code some Command or Reply code
	 *
	 * @return true if the provided code is listened by this Callback
	 */
	public boolean listensTo(final String code) {
		return this.listenedCodes == null || this.listenedCodes.contains(code);
	}

	/**
	 * This method will be called for every received {@link IrcPacket} that
	 * this Callback listens to, or everyone of them if
	 * {@link #listenedCodes} is null.
	 * <p/>
	 * If the method returns false, this means that the passed
	 * {@link IrcPacket} is not the one which was awaited. The Callback will
	 * continue to receive {@link IrcPacket}s to handle.
	 * <p/>
	 * If the method returns true, this means that the passed
	 * {@link IrcPacket} is the awaited one. The Callback will stop receiving
	 * {@link IrcPacket} and will be destroyed.
	 * <p/>
	 * Please @see #onTimeout()
	 *
	 * @param packet a received IrcPacket matching {@link #listenedCodes} if
	 *               defined, any received IrcPacket otherwise
	 *
	 * @return true if the received IrcPacket was the one which was awaited,
	 * false otherwise
	 */
	public abstract boolean onIrcPacket(final IrcPacket packet);

	/**
	 * This method will be called when this Callback times out.
	 * <p/>
	 * The default implementation is to log a warning message.
	 */
	public void onTimeout() {
		LOGGER.warn("A Callback timed out! It had a timeout of " + format.format(getTimeoutDuration() / 1000.0) +
		            " seconds, and its original IRC Packet is '" + this.originalIrcPacket + "'");
	}

}
