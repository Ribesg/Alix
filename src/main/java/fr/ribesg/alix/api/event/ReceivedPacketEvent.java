package fr.ribesg.alix.api.event;

import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.message.IrcPacket;

public class ReceivedPacketEvent extends Event {

   private final Server    source;
   private final IrcPacket packet;

   public ReceivedPacketEvent(final Server source, final IrcPacket packet) {
      this.source = source;
      this.packet = packet;
   }

   public Server getSource() {
      return source;
   }

   public IrcPacket getPacket() {
      return packet;
   }

   @Override
   public String toString() {
      return source.getName() + " | " + packet.toString();
   }
}
