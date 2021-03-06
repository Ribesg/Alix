/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.api;
import fr.ribesg.alix.api.message.PrivMsgIrcPacket;

/**
 * Represents an entity that can receive Messages on a Server.
 * <p>
 * This is extended by {@link Channel} and {@link Source}.
 *
 * @author Ribesg
 */
public abstract class Receiver {

   /**
    * The Server this Receiver is known on
    */
   protected final Server server;

   /**
    * The name of this Receiver
    */
   protected String name;

   /**
    * Receiver constructor.
    *
    * @param server the Server this Receiver belongs to
    * @param name   the name of the Receiver
    */
   protected Receiver(final Server server, final String name) {
      this.server = server;
      this.name = name;
   }

   /**
    * Gets the Server this Receiver is known on.
    *
    * @return the Server this Receiver is known on
    */
   public Server getServer() {
      return this.server;
   }

   /**
    * Gets the name of this Receiver.
    *
    * @return the name of this Receiver
    */
   public String getName() {
      return this.name;
   }

   /**
    * Sets the name of this Received.
    *
    * @param name the name of the Receiver
    */
   public void setName(final String name) {
      this.name = name;
   }

   /**
    * Sends one or multiple messages to this Receiver.
    *
    * @param messages the message or the messages to send to this Receiver
    */
   public void sendMessage(final String... messages) {
      for (final String message : messages) {
         this.server.send(new PrivMsgIrcPacket(this.getName(), message));
      }
   }

}
