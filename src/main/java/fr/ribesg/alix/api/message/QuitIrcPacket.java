/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 *
 * Project file:    Alix - Alix - QuitIrcPacket.java
 * Full Class name: fr.ribesg.alix.api.message.QuitIrcPacket
 */

package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a QUIT IRC Packet.
 */
public class QuitIrcPacket extends IrcPacket {

   private final String reason;

   public QuitIrcPacket() {
      this(null);
   }

   public QuitIrcPacket(final String reason) {
      super(null, Command.QUIT.name(), reason);
      this.reason = reason;
   }

   /**
    * @return true if this Quit packet holds a reason, false otherwise
    */
   public boolean hasReason() {
      return this.reason != null;
   }

   /**
    * @return the reason hold by this Quit packet if any, null otherwise
    */
   public String getReason() {
      return this.reason;
   }
}
