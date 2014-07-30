/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a NAMES IRC Packet.
 */
public class NamesIrcPacket extends IrcPacket {

   private final String channelName;

   public NamesIrcPacket(final String channelName) {
      super(null, Command.NAMES.name(), null, channelName);
      this.channelName = channelName;
   }

   /**
    * @return this Names packet Channel name
    */
   public String getChannelName() {
      return this.channelName;
   }
}
