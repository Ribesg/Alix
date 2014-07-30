/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 *
 * Project file:    Alix - Alix - ArtUtilTest.java
 * Full Class name: fr.ribesg.alix.api.bot.util.ArtUtilTest
 */

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
