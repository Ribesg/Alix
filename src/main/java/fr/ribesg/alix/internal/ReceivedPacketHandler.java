/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.internal;

import fr.ribesg.alix.api.Client;
import fr.ribesg.alix.api.EventManager;
import fr.ribesg.alix.api.Log;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.event.ReceivedPacketEvent;
import fr.ribesg.alix.api.message.IrcPacket;
import fr.ribesg.alix.internal.handlers.InternalReceivedEventHandler;
import fr.ribesg.alix.internal.thread.AbstractRepeatingThread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class handles received packets.
 *
 * @author Ribesg
 */
public class ReceivedPacketHandler extends AbstractRepeatingThread {

   /**
    * A reference to the Client is always nice to have.
    */
   private final Client client;

   /**
    * The Queue of received packets, populated by
    * {@link fr.ribesg.alix.internal.network.SocketReceiver}
    */
   private final BlockingQueue<ReceivedPacketEvent> packetBuffer;

   /**
    * The internal handler for ReceivedPacketEvents
    */
   private final InternalReceivedEventHandler eventHandler;

   /**
    * Constructor
    *
    * @param client the Client this Handler relates to
    */
   public ReceivedPacketHandler(final Client client) {
      super("MsgHandler", 10);
      this.client = client;
      this.packetBuffer = new LinkedBlockingQueue<>();
      this.eventHandler = new InternalReceivedEventHandler(client);
   }

   /**
    * Parses and queues an incoming packet.
    *
    * @param server       the server the packet comes from
    * @param packetString the packet
    */
   public void queue(final Server server, final String packetString) {
      Log.debug("Queue packet " + packetString);
      final IrcPacket packet;
      try {
         packet = IrcPacket.parseMessage(packetString);
         this.packetBuffer.add(new ReceivedPacketEvent(server, packet));
      } catch (final IllegalArgumentException e) {
         Log.error("Failed to parse incoming packet: " + packetString, e);
      }
   }

   /**
    * Polls every packets in the queue and calls events.
    */
   @Override
   public void work() {
      ReceivedPacketEvent event;
      while ((event = this.packetBuffer.poll()) != null) {
         Log.debug("Poll packet " + event.getPacket());
         EventManager.call(event);
      }
   }
}
