/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a JOIN IRC Packet.
 */
public class JoinIrcPacket extends IrcPacket {

   private final String channelName;
   private final String password;

   /**
    * Main constructor.
    *
    * @param channelName the Channel name
    */
   public JoinIrcPacket(final String channelName) {
      super(null, Command.JOIN.name(), null, channelName);
      this.channelName = channelName;
      this.password = null;
   }

   /**
    * Password-protected Channel constructor.
    *
    * @param channelName the Channel name
    * @param password    the password for this Channel
    */
   public JoinIrcPacket(final String channelName, final String password) {
      super(null, Command.JOIN.name(), null, channelName, password);
      this.channelName = channelName;
      this.password = password;
   }

   /**
    * @return this Join packet Channel name
    */
   public String getChannelName() {
      return this.channelName;
   }

   /**
    * @return true if this Join packet contains a password, false otherwise
    */
   public boolean hasPassword() {
      return this.password != null;
   }

   /**
    * @return the password for this Channel if any, null otherwise
    */
   public String getPassword() {
      return this.password;
   }
}
