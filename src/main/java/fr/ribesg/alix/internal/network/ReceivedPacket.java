/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 *
 * Project file:    Alix - Alix - ReceivedPacket.java
 * Full Class name: fr.ribesg.alix.internal.network.ReceivedPacket
 */

package fr.ribesg.alix.internal.network;
import fr.ribesg.alix.api.Server;

/**
 * Represents a Received Packet as the Server from which it comes
 * and the String packet.
 */
public class ReceivedPacket {

   private final Server source;
   private final String packet;

   public ReceivedPacket(Server source, String packet) {
      this.source = source;
      this.packet = packet;
   }

   public Server getSource() {
      return source;
   }

   public String getPacket() {
      return packet;
   }
}
