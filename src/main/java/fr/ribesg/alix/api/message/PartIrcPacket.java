/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 *
 * Project file:    Alix - Alix - PartIrcPacket.java
 * Full Class name: fr.ribesg.alix.api.message.PartIrcPacket
 */

package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a PART IRC Packet.
 */
public class PartIrcPacket extends IrcPacket {

   private final String channelName;

   /**
    * Main constructor.
    *
    * @param channelName the Channel name
    */
   public PartIrcPacket(final String channelName) {
      super(null, Command.PART.name(), null, channelName);
      this.channelName = channelName;
   }

   /**
    * @return this Part packet Channel name
    */
   public String getChannelName() {
      return this.channelName;
   }
}
