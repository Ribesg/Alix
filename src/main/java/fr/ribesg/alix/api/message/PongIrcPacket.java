/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 *
 * Project file:    Alix - Alix - PongIrcPacket.java
 * Full Class name: fr.ribesg.alix.api.message.PongIrcPacket
 */

package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a PONG IRC Packet.
 */
public class PongIrcPacket extends IrcPacket {

   private final String value;

   public PongIrcPacket(final String value) {
      super(null, Command.PONG.name(), value);
      this.value = value;
   }

   /**
    * @return this Pong packet value
    */
   public String getValue() {
      return this.value;
   }
}
