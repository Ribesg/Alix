/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.internal.network;

import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.message.IrcPacket;

/**
 * Represents a Received Packet as the Server from which it comes
 * and the String packet.
 */
public class ReceivedPacketEvent {

   private final Server source;
   private final IrcPacket packet;
   private boolean consumed;

   public ReceivedPacketEvent(final Server source, final String packet) {
      this.source = source;
      this.packet = IrcPacket.parseMessage(packet);
      this.consumed = false;
   }

   public Server getServer() {
      return this.source;
   }

   public IrcPacket getPacket() {
      return this.packet;
   }

   public boolean isConsumed() {
      return this.consumed;
   }

   public void consume() {
      this.consumed = true;
   }

   @Override
   public String toString() {
      return "ReceivedPacketEvent{" +
         "source=" + source +
         ", packet=" + packet +
         ", consumed=" + consumed +
         '}';
   }
}
