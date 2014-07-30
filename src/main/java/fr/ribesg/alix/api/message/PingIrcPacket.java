/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a PONG IRC Packet.
 */
public class PingIrcPacket extends IrcPacket {

   private final String value;

   public PingIrcPacket(final String value) {
      super(null, Command.PING.name(), value);
      this.value = value;
   }

   /**
    * @return this Ping packet value
    */
   public String getValue() {
      return this.value;
   }
}
