package fr.ribesg.alix.api.callback;
import fr.ribesg.alix.api.Log;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.message.IrcPacket;
import fr.ribesg.alix.internal.thread.SimpleCondition;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a Callback for a sent IrcPacket.
 */
public abstract class Callback {

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
	 * A mutex that can be unlocked later
	 */
	protected final SimpleCondition condition;

	/**
	 * This Callback's Priority
	 */
	protected final CallbackPriority priority;

	/**
	 * The Original IRC Packet for which the Callback was set up.
	 * <p>
	 * Should be set by the send-like methods to be more user-friendly than
	 * the User having to repeat the original IRC Packet twice in the method
	 * call.
	 */
	protected IrcPacket originalIrcPacket;

	/**
	 * The Server the Original IRC Packet was sent to.
	 * <p>
	 * Should be set by the send-like methods to be more user-friendly than
	 * the User having to repeat the Server twice in the method
	 * call.
	 */
	protected Server server;

	/**
	 * Main Callback constructor with all arguments.
	 * <p>
	 * Pass some {@link fr.ribesg.alix.api.enums.Command} and/or some
	 * {@link fr.ribesg.alix.api.enums.Reply} codes to it to restrict
	 * calls to {@link #onIrcPacket(IrcPacket)} to them.
	 * <p>
	 * If no argument is passed, {@link #onIrcPacket(IrcPacket)} will be
	 * called for every incoming {@link IrcPacket} until the method
	 * returns true.
	 * <p>
	 * Of course listened Codes have to be uppercase to follow IRC RFCs.
	 *
	 * @param priority        the priority of this Callback
	 * @param timeoutDuration the time after which this Callback should call
	 *                        {@link #onTimeout} and be destroyed, in
	 *                        milliseconds
	 * @param condition       a condition to signal when the Callback
	 *                        execution is done
	 * @param listenedCodes   listened Commands and Reply codes, can be empty
	 *                        to listen to everything
	 */
	public Callback(final CallbackPriority priority, final long timeoutDuration, final SimpleCondition condition, final String... listenedCodes) {
		this.priority = priority;
		this.timeoutDuration = timeoutDuration;
		this.timeoutDate = System.currentTimeMillis() + timeoutDuration;
		if (listenedCodes.length != 0) {
			this.listenedCodes = new HashSet<>();
			Collections.addAll(this.listenedCodes, listenedCodes);
		} else {
			this.listenedCodes = null;
		}
		this.condition = condition;
	}

	/**
	 * Constructor without condition.
	 * <p>
	 * Pass some {@link fr.ribesg.alix.api.enums.Command} and/or some
	 * {@link fr.ribesg.alix.api.enums.Reply} codes to it to restrict
	 * calls to {@link #onIrcPacket(IrcPacket)} to them.
	 * <p>
	 * If listenedCodes is empty, {@link #onIrcPacket(IrcPacket)} will be
	 * called for every incoming {@link IrcPacket} until the method
	 * returns true.
	 * <p>
	 * Of course listened Codes have to be uppercase to follow IRC RFCs.
	 *
	 * @param priority        the priority of this Callback
	 * @param timeoutDuration the time after which this Callback should call
	 *                        {@link #onTimeout} and be destroyed, in
	 *                        milliseconds
	 * @param listenedCodes   listened Commands and Reply codes, can be empty
	 *                        to listen to everything
	 */
	public Callback(final CallbackPriority priority, final long timeoutDuration, final String... listenedCodes) {
		this(priority, timeoutDuration, null, listenedCodes);
	}

	/**
	 * Constructor with default timeout of 30 seconds.
	 * <p>
	 * Pass some {@link fr.ribesg.alix.api.enums.Command} and/or some
	 * {@link fr.ribesg.alix.api.enums.Reply} codes to it to restrict
	 * calls to {@link #onIrcPacket(IrcPacket)} to them.
	 * <p>
	 * If listenedCodes is empty, {@link #onIrcPacket(IrcPacket)} will be
	 * called for every incoming {@link IrcPacket} until the method
	 * returns true.
	 * <p>
	 * Of course listened Codes have to be uppercase to follow IRC RFCs.
	 *
	 * @param priority      the priority of this Callback
	 * @param condition     a condition to signal when the Callback
	 *                      execution is done
	 * @param listenedCodes listened Commands and Reply codes, can be empty
	 *                      to listen to everything
	 */
	public Callback(final CallbackPriority priority, final SimpleCondition condition, final String... listenedCodes) {
		this(priority, DEFAULT_TIMEOUT, condition, listenedCodes);
	}

	/**
	 * Constructor with default timeout of 30 seconds and without condition.
	 * <p>
	 * Pass some {@link fr.ribesg.alix.api.enums.Command} and/or some
	 * {@link fr.ribesg.alix.api.enums.Reply} codes to it to restrict
	 * calls to {@link #onIrcPacket(IrcPacket)} to them.
	 * <p>
	 * If no argument is passed, {@link #onIrcPacket(IrcPacket)} will be
	 * called for every incoming {@link IrcPacket} until the method
	 * returns true.
	 * <p>
	 * Of course listened Codes have to be uppercase to follow IRC RFCs.
	 *
	 * @param priority      the priority of this Callback
	 * @param listenedCodes listened Commands and Reply codes, can be empty
	 *                      to listen to everything
	 */
	public Callback(final CallbackPriority priority, final String... listenedCodes) {
		this(priority, DEFAULT_TIMEOUT, null, listenedCodes);
	}

	/**
	 * Constructor with default {@link CallbackPriority#LOW} priority.
	 * <p>
	 * Pass some {@link fr.ribesg.alix.api.enums.Command} and/or some
	 * {@link fr.ribesg.alix.api.enums.Reply} codes to it to restrict
	 * calls to {@link #onIrcPacket(IrcPacket)} to them.
	 * <p>
	 * If no argument is passed, {@link #onIrcPacket(IrcPacket)} will be
	 * called for every incoming {@link IrcPacket} until the method
	 * returns true.
	 * <p>
	 * Of course listened Codes have to be uppercase to follow IRC RFCs.
	 *
	 * @param timeoutDuration the time after which this Callback should call
	 *                        {@link #onTimeout} and be destroyed, in
	 *                        milliseconds
	 * @param condition       a condition to signal when the Callback
	 *                        execution is done
	 * @param listenedCodes   listened Commands and Reply codes, can be empty
	 *                        to listen to everything
	 */
	public Callback(final long timeoutDuration, final SimpleCondition condition, final String... listenedCodes) {
		this(CallbackPriority.LOW, timeoutDuration, condition, listenedCodes);
	}

	/**
	 * Constructor with default {@link CallbackPriority#LOW} priority and
	 * without condition.
	 * <p>
	 * Pass some {@link fr.ribesg.alix.api.enums.Command} and/or some
	 * {@link fr.ribesg.alix.api.enums.Reply} codes to it to restrict
	 * calls to {@link #onIrcPacket(IrcPacket)} to them.
	 * <p>
	 * If listenedCodes is empty, {@link #onIrcPacket(IrcPacket)} will be
	 * called for every incoming {@link IrcPacket} until the method
	 * returns true.
	 * <p>
	 * Of course listened Codes have to be uppercase to follow IRC RFCs.
	 *
	 * @param timeoutDuration the time after which this Callback should call
	 *                        {@link #onTimeout} and be destroyed, in
	 *                        milliseconds
	 * @param listenedCodes   listened Commands and Reply codes, can be empty
	 *                        to listen to everything
	 */
	public Callback(final long timeoutDuration, final String... listenedCodes) {
		this(CallbackPriority.LOW, timeoutDuration, null, listenedCodes);
	}

	/**
	 * Constructor with default {@link CallbackPriority#LOW} priority
	 * and with a default timeout of 30 seconds.
	 * <p>
	 * Pass some {@link fr.ribesg.alix.api.enums.Command} and/or some
	 * {@link fr.ribesg.alix.api.enums.Reply} codes to it to restrict
	 * calls to {@link #onIrcPacket(IrcPacket)} to them.
	 * <p>
	 * If listenedCodes is empty, {@link #onIrcPacket(IrcPacket)} will be
	 * called for every incoming {@link IrcPacket} until the method
	 * returns true.
	 * <p>
	 * Of course listened Codes have to be uppercase to follow IRC RFCs.
	 *
	 * @param condition     a condition to signal when the Callback
	 *                      execution is done
	 * @param listenedCodes listened Commands and Reply codes, can be empty
	 *                      to listen to everything
	 */
	public Callback(final SimpleCondition condition, final String... listenedCodes) {
		this(CallbackPriority.LOW, DEFAULT_TIMEOUT, condition, listenedCodes);
	}

	/**
	 * Constructor with default {@link CallbackPriority#LOW} priority,
	 * without condition and with a default timeout of 30 seconds.
	 * <p>
	 * Pass some {@link fr.ribesg.alix.api.enums.Command} and/or some
	 * {@link fr.ribesg.alix.api.enums.Reply} codes to it to restrict
	 * calls to {@link #onIrcPacket(IrcPacket)} to them.
	 * <p>
	 * If no argument is passed, {@link #onIrcPacket(IrcPacket)} will be
	 * called for every incoming {@link IrcPacket} until the method
	 * returns true.
	 * <p>
	 * Of course listened Codes have to be uppercase to follow IRC RFCs.
	 *
	 * @param listenedCodes listened Commands and Reply codes, can be empty
	 *                      to listen to everything
	 */
	public Callback(final String... listenedCodes) {
		this(CallbackPriority.LOW, DEFAULT_TIMEOUT, null, listenedCodes);
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
	 * @return this Callback's priority
	 */
	public CallbackPriority getPriority() {
		return priority;
	}

	/**
	 * @return the Original IRC Packet for which the Callback was set up
	 */
	public IrcPacket getOriginalIrcPacket() {
		return originalIrcPacket;
	}

	/**
	 * You should not call this.
	 * <p>
	 * This should only be called by the Server's send-like methods to link
	 * the original IrcPacket to its Callback, for the user not to have to
	 * do it.
	 */
	public void setOriginalIrcPacket(final IrcPacket originalIrcPacket) {
		this.originalIrcPacket = originalIrcPacket;
	}

	/**
	 * You should not call this.
	 * <p>
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
	 * <p>
	 * WARNING: This method will be called synchronously! Anything that does
	 * not need to be sync should be called async from this method!
	 * <p>
	 * If the method returns false, this means that the Callback want to
	 * continue to receive {@link IrcPacket}s to handle: its job is not
	 * done.
	 * <p>
	 * If the method returns true, this means that the Callback wants to be
	 * destroyed: its job is done.
	 * <p>
	 * Please @see #onTimeout()
	 *
	 * @param packet a received IrcPacket matching {@link #listenedCodes} if
	 *               defined, any received IrcPacket otherwise
	 *
	 * @return true if the Callback's job is done, false otherwise
	 */
	public abstract boolean onIrcPacket(final IrcPacket packet);

	/**
	 * This method will be called when this Callback times out.
	 * <p>
	 * The default implementation is to log a warning message.
	 */
	// TODO This should maybe throw an Exception? With list of deadlocked threads maybe?
	public void onTimeout() {
		Log.warn("A Callback timed out! It had a timeout of " + format.format(getTimeoutDuration() / 1000.0) +
		         " seconds, and its original IRC Packet is '" + this.originalIrcPacket + "'");
		this.done();
	}

	/**
	 * Unlock this Callback's mutex, if any
	 */
	public void done() {
		if (this.condition != null) {
			this.condition.signalAll();
		}
	}

}
