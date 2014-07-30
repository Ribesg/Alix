/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 *
 * Project file:    Alix - Alix - UserIrcPacket.java
 * Full Class name: fr.ribesg.alix.api.message.UserIrcPacket
 */

package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a USER IRC Packet.
 */
public class UserIrcPacket extends IrcPacket {

   private final String userName;
   private final String realName;

   public UserIrcPacket(final String userName) {
      this(userName, null);
   }

   public UserIrcPacket(final String userName, final String realName) {
      super(null, Command.USER.name(), realName != null ? realName : userName, userName, ".", ".");
      this.userName = userName;
      this.realName = realName;
   }

   /**
    * @return this User packet User name
    */
   public String getUserName() {
      return this.userName;
   }

   /**
    * @return true if this User packet holds a Real name, false otherwise
    */
   public boolean hasRealName() {
      return this.realName != null;
   }

   /**
    * @return this User packet Real name if any, null otherwise
    */
   public String getRealName() {
      return this.realName;
   }
}
