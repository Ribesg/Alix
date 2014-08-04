/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.internal.network;
import fr.ribesg.alix.api.Log;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.internal.thread.AbstractRepeatingThread;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * This class handles sending packets.
 * TODO: All the docz
 *
 * @author Ribesg
 */
public class SocketSender extends AbstractRepeatingThread {

   private final BufferedWriter writer;
   private final Deque<String>  buffer;

   private final Server server;

   /* package */ SocketSender(final Server server, final BufferedWriter writer) {
      super(" S-Sender ", 50);
      this.writer = writer;
      this.buffer = new ConcurrentLinkedDeque<>();
      this.server = server;
   }

   @Override
   public void work() throws InterruptedException {
      String mes;
      try {
         while ((mes = this.buffer.poll()) != null) {
            Log.debug(server.getUrl() + ':' + server.getPort() +
                      " - SENDING MESSAGE: '" + mes.replace("\n", "\\n").replace("\r", "\\r") + "'");
            this.writer.write(mes);
            if (buffer.isEmpty()) {
               this.writer.flush();
               Thread.sleep(1_000);
            }
         }
      } catch (final IOException e) {
         Log.error("Failed to send IRC Packet", e);
      }
   }

   public void write(final String message) {
      this.buffer.offer(message);
   }

   public void writeFirst(final String message) {
      this.buffer.offerFirst(message);
   }

   /* package */ boolean hasAnythingToWrite() {
      return !this.buffer.isEmpty();
   }

   /* package */ void kill() {
      try {
         this.writer.close();
      } catch (final IOException e) {
         Log.error("Failed to close Writer stream", e);
      }
   }
}
