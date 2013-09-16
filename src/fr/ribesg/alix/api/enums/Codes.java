package fr.ribesg.alix.api.enums;
import java.text.DecimalFormat;

/**
 * This Enum provides convenient access to all IRC special Strings,
 * like Color codes, formatting codes, and others.
 * <p/>
 * Color codes and formatting codes are not defined by any real convention,
 * and may not work on all clients.
 * This API is based on the mIRC character codes for colors and formatting.
 *
 * @author Ribesg
 */
public enum Codes {

	// ########## //
	// Formatting //
	// ########## //

	BOLD(get(0x02)),
	ITALIC(get(0x09)),
	STRIKETHROUGH(get(0x13)),
	UNDERLINE(get(0x1F)),
	UNDERLINE2(get(0x15)),
	REVERSE(get(0x16)),

	// ###### //
	// Colors //
	// ###### //

	WHITE(Codes.COLOR_CODE + Color.WHITE),
	BLACK(Codes.COLOR_CODE + Color.BLACK),
	BLUE(Codes.COLOR_CODE + Color.BLUE),
	GREEN(Codes.COLOR_CODE + Color.GREEN),
	RED(Codes.COLOR_CODE + Color.RED),
	BROWN(Codes.COLOR_CODE + Color.BROWN),
	PURPLE(Codes.COLOR_CODE + Color.PURPLE),
	ORANGE(Codes.COLOR_CODE + Color.ORANGE),
	YELLOW(Codes.COLOR_CODE + Color.YELLOW),
	LIGHT_GREEN(Codes.COLOR_CODE + Color.LIGHT_GREEN),
	TEAL(Codes.COLOR_CODE + Color.TEAL),
	LIGHT_CYAN(Codes.COLOR_CODE + Color.LIGHT_CYAN),
	LIGHT_BLUE(Codes.COLOR_CODE + Color.LIGHT_BLUE),
	PINK(Codes.COLOR_CODE + Color.PINK),
	GREY(Codes.COLOR_CODE + Color.GREY),
	LIGHT_GREY(Codes.COLOR_CODE + Color.LIGHT_GREY),

	// ##################### //
	// Reset colors & format //
	// ##################### //

	RESET(get(0x0f)),

	// ########### //
	// Other codes //
	// ########### //

	/**
	 * Space character, used to separate prefix, command and parameters in
	 * IRC messages
	 */
	SP(get(0x20)),

	/** Carriage return, used to separate different IRC messages */
	CRLF(get(0x0D) + get(0x0A)),

	/**
	 * ASCII Colon, used as first character of any IRC message. Is not
	 * separated from the prefix by SP
	 */
	COLON(get(0x3b)),

	/**
	 * 'Blank' character. Will not appear in clients that supports UTF-8.
	 * Used to prevent pinging someone by inserting this character into its
	 * name
	 */
	EMPTY(get(0x200B));

	// ###################### //
	// ## END OF ENUM LIST ## //
	// ###################### //

	/**
	 * Code used in color codes. A valid color code is composed of this code
	 * + the color number
	 */
	private static final String COLOR_CODE = get(0x03);

	/**
	 * Transform a char into a String
	 *
	 * @param charCode the integer code of the char
	 *
	 * @return a String containing the char
	 */
	private static String get(int charCode) {
		return Character.toString((char) charCode);
	}

	/**
	 * This enum provides a list of color number, their number being their
	 * ordinal
	 */
	private enum Color {
		WHITE,
		BLACK,
		BLUE,
		GREEN,
		RED,
		BROWN,
		PURPLE,
		ORANGE,
		YELLOW,
		LIGHT_GREEN,
		TEAL,
		LIGHT_CYAN,
		LIGHT_BLUE,
		PINK,
		GREY,
		LIGHT_GREY;

		/**
		 * Allows to have 2-chars number, even if it's in the 0-9 range
		 * (00-09)
		 */
		private static final DecimalFormat FORMAT = new DecimalFormat("00");

		/** @return a String of 2 chars containing the color number */
		public String toString() {
			return FORMAT.format(this.ordinal());
		}
	}

	/** The code that this Enum value represents. */
	private String code;

	private Codes(String code) {
		this.code = code;
	}

	public String toString() {
		return code;
	}
}
