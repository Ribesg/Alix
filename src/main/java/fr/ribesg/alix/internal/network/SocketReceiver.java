/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.internal.network;
import fr.ribesg.alix.api.Log;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.internal.ReceivedPacketHandler;
import fr.ribesg.alix.internal.thread.AbstractRepeatingThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * This class handles receiving packets.
 * TODO: All the docz
 *
 * @author Ribesg
 */
public class SocketReceiver extends AbstractRepeatingThread {

   private final BufferedReader reader;

   private final Server                server;
   private final ReceivedPacketHandler packetHandler;

   /* package */ SocketReceiver(final Server server, final BufferedReader reader, final ReceivedPacketHandler packetHandler) {
      super("S-Receiver", 10);
      this.reader = reader;
      this.server = server;
      this.packetHandler = packetHandler;
   }

   @Override
   public void work() {
      String mes;
      try {
         while ((mes = this.reader.readLine()) != null) {
            Log.debug(server.getUrl() + ':' + server.getPort() + " - RECEIVED MESSAGE: '" + mes + "'");
            this.packetHandler.queue(this.server, mes);
         }
      } catch (final SocketTimeoutException ignored) {
         // readLine() Timeout
      } catch (final IOException e) {
         Log.error("IOException caught when reading from Socket", e);
      }
   }

   /* package */ void kill() {
      try {
         this.reader.close();
      } catch (final IOException e) {
         Log.error("Failed to close Reader stream", e);
      }
   }
}
