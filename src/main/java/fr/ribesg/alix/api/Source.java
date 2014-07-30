/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.api;

/**
 * Represents the Source of a Message, most of the time
 * written in the Prefix part of the Message.
 * <p>
 * This can be either a Server or a User.
 */
public class Source extends Receiver {

   /**
    * The User name of this Source, if any
    */
   private final String userName;

   /**
    * The Host name of this Source, if any
    */
   private final String hostName;

   /**
    * If this Source is a User
    */
   private final boolean isUser;

   /**
    * User Source constructor.
    *
    * @param server   the Server this Source belongs to
    * @param name     the name of the Source
    * @param userName the User name of the Source
    * @param hostName the Host name of the Source
    */
   public Source(final Server server, final String name, final String userName, final String hostName) {
      super(server, name);
      this.userName = userName;
      this.hostName = hostName;
      this.isUser = true;
   }

   /**
    * Server Source constructor.
    *
    * @param server   the Server this Source belongs to
    * @param hostName the Host name / Name of the Source
    */
   public Source(final Server server, final String hostName) {
      super(server, hostName);
      this.userName = null;
      this.hostName = hostName;
      this.isUser = false;
   }

   /**
    * If this Source if a User, this will return the User name of this User
    * if it's known, null if it isn't.
    * If this Source is not a User, this will return null.
    *
    * @return the User name of this Source if any, null otherwise
    */
   public String getUserName() {
      return this.userName;
   }

   /**
    * If this Source is a User, this will return the Host name of this User
    * if it's known, null if it isn't.
    * If this Source is not a User, this will return the exact same thing
    * than {@link #getName()}.
    *
    * @return the Host name of this Source if any, null otherwise
    */
   public String getHostName() {
      return this.hostName;
   }

   /**
    * @return true if this Source is a User, false otherwise
    */
   public boolean isUser() {
      return this.isUser;
   }

   /**
    * @return true if this Source is a Server, false otherwise
    */
   public boolean isServer() {
      return !this.isUser;
   }
}
