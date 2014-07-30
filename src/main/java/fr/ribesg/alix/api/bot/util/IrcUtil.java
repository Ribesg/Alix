/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.api.bot.util;
import fr.ribesg.alix.api.enums.Codes;

public class IrcUtil {

   /**
    * Inserts an empty char in every word of the provided message.
    *
    * @param message the message to silence
    *
    * @return the silenced message
    */
   public static String preventPing(final String message) {
      if (message == null || message.length() < 2) {
         return message;
      } else {
         final String trimedMessage = message.trim();
         String result = trimedMessage.substring(0, 1) + Codes.EMPTY + trimedMessage.substring(1);

         int i = -1;
         while ((i = result.indexOf(' ', i + 1)) != -1 && i + 2 < result.length()) {
            if (result.charAt(i + 1) != ' ' && result.charAt(i + 2) != ' ') {
               // Insert 'empty' character
               result = result.substring(0, i + 2) + Codes.EMPTY + result.substring(i + 2);
            }
         }

         return result;
      }
   }
}
