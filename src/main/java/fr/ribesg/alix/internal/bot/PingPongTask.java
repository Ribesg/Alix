/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.internal.bot;

import fr.ribesg.alix.api.Client;
import fr.ribesg.alix.api.EventManager;
import fr.ribesg.alix.api.Log;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.callback.Callback;
import fr.ribesg.alix.api.enums.Command;
import fr.ribesg.alix.api.event.ClientLostConnectionEvent;
import fr.ribesg.alix.api.event.ReceivedPacketEvent;
import fr.ribesg.alix.api.message.PingIrcPacket;
import fr.ribesg.alix.internal.thread.AbstractRepeatingThread;

import java.util.Random;

/**
 * This task will handle the Ping-Pong thing, to make sure
 * we're still connected.
 */
public class PingPongTask extends AbstractRepeatingThread {

   private static final Random RANDOM = new Random();

   private final Client client;

   public PingPongTask(final Client client) {
      super("PingPong  ", 120_000);
      this.client = client;
   }

   @Override
   public void work() {
      this.client.getServers().stream().filter(Server::isConnected).forEach(server -> {
         final String value = Long.toString(RANDOM.nextLong());
         server.send(new PingIrcPacket(value), new PingPongCallback(value));
      });
   }

   private class PingPongCallback extends Callback {

      private String value;

      private PingPongCallback(final String value) {
         super(60_000, Command.PONG.name());
         this.value = value;
      }

      @Override
      public boolean onReceivedPacket(final ReceivedPacketEvent event) {
         if (this.value.equals(event.getPacket().getTrail())) {
            event.consume();
            return true;
         } else {
            return false;
         }
      }

      @Override
      public void onTimeout() {
         Log.info("Failed to receive PONG response in time, disconnecting from server " + this.server.getName());
         this.server.disconnect();
         Client.getThreadPool().submit(() -> EventManager.call(new ClientLostConnectionEvent(this.server)));
      }
   }
}
