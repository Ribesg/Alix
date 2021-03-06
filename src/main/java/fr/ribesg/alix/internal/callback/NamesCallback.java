/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.internal.callback;

import fr.ribesg.alix.api.Channel;
import fr.ribesg.alix.api.Log;
import fr.ribesg.alix.api.callback.Callback;
import fr.ribesg.alix.api.enums.Codes;
import fr.ribesg.alix.api.enums.Reply;
import fr.ribesg.alix.api.event.ReceivedPacketEvent;
import fr.ribesg.alix.api.message.IrcPacket;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class represents an internal Callback for the NAMES IRC Command.
 * <p>
 * The idea for this Callback is to handle the retrieval of Users of a
 * Channel properly.
 */
public class NamesCallback extends Callback {

   private static final String[] LISTENED_CODES = new String[] {
      Reply.RPL_NAMREPLY.getIntCodeAsString(),
      Reply.RPL_ENDOFNAMES.getIntCodeAsString()
   };

   private final Channel     channel;
   private final Set<String> users;

   public NamesCallback(final Channel channel) {
      super(LISTENED_CODES);
      this.channel = channel;
      this.users = new HashSet<>();
   }

   public NamesCallback(final Channel channel, final List<Runnable> callbacks) {
      this(channel);
      this.callbacks.addAll(callbacks);
   }

   @Override
   public boolean onReceivedPacket(final ReceivedPacketEvent event) {
      Log.debug("Received packet " + event);
      final IrcPacket packet = event.getPacket();
      String channelName;
      switch (Reply.getFromCode(packet.getRawCommandString())) {
         case RPL_NAMREPLY: // A part of the complete Users Set
            channelName = packet.getParameters()[2];
            if (this.channel.getName().equals(channelName)) {
               Log.debug("Handled, adding to the list");
               final String[] users = packet.getTrail().split(Codes.SP);
               Collections.addAll(this.users, users);
               event.consume();
            }
            return false;
         case RPL_ENDOFNAMES: // Notification of the End of the Users Set
            channelName = packet.getParameters()[1];
            if (this.channel.getName().equals(channelName)) {
               Log.debug("Handled, unlocking");
               this.channel.setUsers(this.users);
               this.runAllCallbacks();
               event.consume();
               return true;
            } else {
               return false;
            }
         default:
            throw new IllegalArgumentException(packet.toString());
      }
   }

   @Override
   public void onTimeout() {
      Log.error("NAMES Command timed out! The users list of Channel " + this.channel.getName() + " has been emptied!");
      this.channel.clearUsers();
   }
}
