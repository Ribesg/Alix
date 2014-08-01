/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.internal.callback;

import fr.ribesg.alix.api.Client;
import fr.ribesg.alix.api.Log;
import fr.ribesg.alix.api.callback.Callback;
import fr.ribesg.alix.api.callback.CallbackPriority;
import fr.ribesg.alix.api.message.IrcPacket;
import fr.ribesg.alix.internal.thread.AbstractRepeatingThread;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * This class handles Callbacks.
 */
public class CallbackHandler {

   /**
    * The registered Callbacks, per priority, sorted by closest timeout first.
    */
   private final Map<CallbackPriority, Queue<Callback>> prioritizedCallbacks;

   private final CallbacksCleanerThread cleanerThread;

   /**
    * Main CallbackHandler constructor.
    */
   public CallbackHandler() {
      final Map<CallbackPriority, Queue<Callback>> map = new EnumMap<>(CallbackPriority.class);
      map.put(CallbackPriority.HIGHEST, new ConcurrentLinkedDeque<>());
      map.put(CallbackPriority.HIGH, new ConcurrentLinkedDeque<>());
      map.put(CallbackPriority.LOW, new ConcurrentLinkedDeque<>());
      map.put(CallbackPriority.LOWEST, new ConcurrentLinkedDeque<>());
      this.prioritizedCallbacks = Collections.unmodifiableMap(map);
      this.cleanerThread = new CallbacksCleanerThread(this.prioritizedCallbacks);

      this.cleanerThread.start();
   }

   /**
    * Kill this CallbackHandler
    */
   public void kill() {
      this.cleanerThread.askStop();
      try {
         this.cleanerThread.join();
      } catch (final InterruptedException e) {
         Log.error(e.getMessage(), e);
      }
   }

   /**
    * Register a new Callback.
    *
    * @param callback the callback to register
    */
   public void registerCallback(final Callback callback) {
      this.prioritizedCallbacks.get(callback.getPriority()).add(callback);
   }

   /**
    * See if a Callback handles an incoming IRC Packet.
    *
    * @param packet the incoming IRC Packet
    */
   public void handle(final CallbackPriority priority, final IrcPacket packet) {
      final Iterable<Callback> callbacks = this.prioritizedCallbacks.get(priority);
      final String code = packet.getRawCommandString().toUpperCase();
      final long now = System.currentTimeMillis();
      final Iterator<Callback> it = callbacks.iterator();
      while (it.hasNext()) {
         final Callback callback = it.next();
         if (callback.getTimeoutDate() < now) {
            Client.getThreadPool().submit(callback::onTimeout);
            it.remove();
         } else if (callback.listensTo(code)) {
            if (callback.onIrcPacket(packet)) {
               Log.debug("DEBUG: Packet handled by a Callback!");
               it.remove();
            }
         }
      }
   }

   /**
    * The purpose of this Thread is to take a SortedSet of
    * Callbacks and to check that every Callback is still valid,
    * i.e. every Callback didn't timeout.
    */
   private class CallbacksCleanerThread extends AbstractRepeatingThread {

      /**
       * The callbacks
       */
      private final Map<CallbackPriority, ? extends Iterable<Callback>> prioritizedCallbacks;

      /**
       * Main CallbacksCleanerThread constructor.
       *
       * @param prioritizedCallbacks the prioritizedCallbacks to monitor
       */
      public CallbacksCleanerThread(final Map<CallbackPriority, ? extends Iterable<Callback>> prioritizedCallbacks) {
         super("Cb-Cleaner", 1_000);
         this.prioritizedCallbacks = prioritizedCallbacks;
      }

      /**
       * Will check the prioritizedCallbacks Set every second
       */
      @Override
      public void work() {
         for (final CallbackPriority priority : this.prioritizedCallbacks.keySet()) {
            final Iterable<Callback> callbacks = this.prioritizedCallbacks.get(priority);
            final Iterator<Callback> it = callbacks.iterator();
            if (it.hasNext()) {
               final long now = System.currentTimeMillis();
               boolean removedCallback;
               do {
                  removedCallback = false;
                  final Callback callback = it.next();
                  if (callback.getTimeoutDate() < now) {
                     callback.onTimeout();
                     it.remove();
                     removedCallback = true;
                  }
               } while (it.hasNext() && removedCallback);
            }
         }
      }
   }

}
