package fr.ribesg.alix.api.bot.util;
import fr.ribesg.alix.api.enums.Codes;

public class ArtUtil {

	/**
	 * Return a colorized String containing nbChars c characters, with left
	 * percent of them colored with leftColor and right percent of them
	 * colored with rightColor.
	 *
	 * @param leftPercentage     the percentage on the left to be colored
	 *                           with leftColor
	 * @param leftColor          the left color
	 * @param rightPercentage    the percentage on the right to be colored
	 *                           with right color
	 * @param rightColor         the right color
	 * @param totalNbChars       the number of characters
	 * @param barCharacter       the character to use
	 * @param separatorCharacter the seperator
	 * @param separatorColor     the seperator color
	 *
	 * @return the ASCII bar
	 */
	public static String asciiBar(final double leftPercentage,
	                              final String leftColor,
	                              final double rightPercentage,
	                              final String rightColor,
	                              final int totalNbChars,
	                              final char barCharacter,
	                              final char separatorCharacter,
	                              final String separatorColor) {
		final double total = leftPercentage + rightPercentage;
		final int nbLeftChars = (int) Math.round(leftPercentage * totalNbChars / total);
		final int nbRightChars = totalNbChars - nbLeftChars;
		final StringBuilder builder = new StringBuilder();
		builder.append(leftColor);
		for (int i = 0; i < nbLeftChars; i++) {
			builder.append(barCharacter);
		}
		builder.append(separatorColor);
		builder.append(separatorCharacter);
		builder.append(rightColor);
		for (int i = 0; i < nbRightChars; i++) {
			builder.append(barCharacter);
		}
		builder.append(Codes.RESET);
		return builder.toString();
	}

	/**
	 * Build a String of the provided size containing only spaces.
	 *
	 * @param size the required size
	 *
	 * @return a String containing size spaces
	 */
	public static String spaces(final int size) {
		return extendRight("", size);
	}

	/**
	 * Help grow a String to the correct size with spaces on the left.
	 *
	 * @param toExtend    the String to extend
	 * @param finalLength the wanted length
	 *
	 * @return a String containing enough spaces followed by the String
	 * toExtend
	 */
	public static String extendLeft(final String toExtend, final int finalLength) {
		String result = toExtend;
		while (result.length() < finalLength) {
			result = ' ' + result;
		}
		return result;
	}

	/**
	 * Help grow a String to the correct size with spaces on the right.
	 *
	 * @param toExtend    the String to extend
	 * @param finalLength the wanted length
	 *
	 * @return a String containing the String toExtend followed by enough
	 * spaces
	 */
	public static String extendRight(final String toExtend, final int finalLength) {
		String result = toExtend;
		while (result.length() < finalLength) {
			result = result + ' ';
		}
		return result;
	}

	/**
	 * Help grow a String to the correct size with spaces on each side.
	 *
	 * @param toExtend    the String to extend
	 * @param finalLength the wanted length
	 *
	 * @return a String containing the String toExtend with spaces on each
	 * side
	 */
	public static String extendCentered(final String toExtend, final int finalLength) {
		String result = toExtend;
		while (result.length() < finalLength) {
			result = ' ' + result + ' ';
		}
		return result.length() == finalLength ? result : result.substring(1);
	}
}
