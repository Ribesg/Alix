package fr.ribesg.alix.api.bot.util;
import junit.framework.TestCase;
import org.junit.Assert;

public class ArtUtilTest extends TestCase {

	public void testExtendLeft() throws Exception {
		final String input = "Test";
		final int awaitedSize = 10;
		final String awaitedOutput = "      Test";

		final String calculatedOutput = ArtUtil.extendLeft(input, awaitedSize);

		Assert.assertEquals("ArtUtil#extendLeft(...) failed!", awaitedOutput, calculatedOutput);
	}

	public void testExtendRight() throws Exception {
		final String input = "Test";
		final int awaitedSize = 10;
		final String awaitedOutput = "Test      ";

		final String calculatedOutput = ArtUtil.extendRight(input, awaitedSize);

		Assert.assertEquals("ArtUtil#extendRight(...) failed!", awaitedOutput, calculatedOutput);
	}

	public void testExtendCentered() throws Exception {
		// Even test
		final String input = "Test";
		final int awaitedSize = 10;
		final String awaitedOutput = "   Test   ";

		final String calculatedOutput = ArtUtil.extendCentered(input, awaitedSize);

		Assert.assertEquals("ArtUtil#extendCentered(...) failed!", awaitedOutput, calculatedOutput);

		// Odd test
		final String input2 = "Test2";
		final int awaitedSize2 = 10;
		final String awaitedOutput2 = "  Test2   ";

		final String calculatedOutput2 = ArtUtil.extendCentered(input2, awaitedSize2);

		Assert.assertEquals("ArtUtil#extendCentered(...) failed!", awaitedOutput2, calculatedOutput2);
	}
}
