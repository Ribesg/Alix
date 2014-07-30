/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 *
 * Project file:    Alix - Alix - PrivMsgIrcPacket.java
 * Full Class name: fr.ribesg.alix.api.message.PrivMsgIrcPacket
 */

package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a PRIVMSG IRC Packet.
 */
public class PrivMsgIrcPacket extends IrcPacket {

   private final String receiver;
   private final String message;

   public PrivMsgIrcPacket(final String receiver, final String message) {
      super(null, Command.PRIVMSG.name(), message, receiver);
      this.receiver = receiver;
      this.message = message;
   }

   /**
    * @return this PrivMsg packet receiver
    */
   public String getReceiver() {
      return receiver;
   }

   /**
    * @return this PrivMsg packet message
    */
   public String getMessage() {
      return message;
   }
}
