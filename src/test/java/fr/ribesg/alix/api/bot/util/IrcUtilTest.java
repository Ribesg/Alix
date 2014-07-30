/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.api.bot.util;
import fr.ribesg.alix.api.enums.Codes;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class IrcUtilTest {

   private static final Logger LOG = Logger.getLogger(IrcUtilTest.class.getName());

   @Test
   public void testPreventPing() {
      final String originalString = "This is a test message";
      final String awaitedString = "T" + Codes.EMPTY + "his i" + Codes.EMPTY + "s a t" +
                                   Codes.EMPTY + "est m" + Codes.EMPTY + "essage";

      final String calculatedString = IrcUtil.preventPing(originalString);

      if (!awaitedString.equals(calculatedString)) {
         LOG.error("Awaited: " + Arrays.toString(awaitedString.toCharArray()) + " (" + awaitedString + ')');
         LOG.error("Found:   " + Arrays.toString(calculatedString.toCharArray()) + " (" + calculatedString + ')');
         Assert.fail("IrcUtil#preventPing(...) failed!");
      }
   }
}
