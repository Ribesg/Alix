/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix;

import fr.ribesg.alix.api.Log;

/**
 * Just a class used for quick tools like pausing a Thread
 * without taking care of interruptions.
 *
 * @author Ribesg
 */
public class Tools {

   /**
    * Pauses the calling Thread for the provided amount of
    * time. Ignores any InterruptedException, but logs it.
    *
    * @param millis the time to pause
    */
   public static void pause(final int millis) {
      try {
         Thread.sleep(millis);
      } catch (final InterruptedException e) {
         Log.error(e.getMessage(), e);
      }
   }
}
