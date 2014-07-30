/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a MODE IRC Packet.
 */
public class ModeIrcPacket extends IrcPacket {

   /**
    * Used to properly call the super(...) constructor.
    *
    * @param channelName a first parameter
    * @param modeString  a second parameter
    * @param parameters  tons of other parameters
    *
    * @return a merge of all those parameters
    */
   private static String[] mergeArguments(final String channelName, final String modeString, final String... parameters) {
      final String[] result = new String[parameters.length + 2];
      result[0] = channelName;
      result[1] = modeString;
      System.arraycopy(parameters, 0, result, 2, parameters.length);
      return result;
   }

   private final String entityName;

   private final String modeString;

   private final String[] parameters;

   /**
    * Short MODE Packet constructor.
    *
    * @param entityName the entity of the Mode packet i.e. a Channel name or
    *                   a User name
    */
   public ModeIrcPacket(final String entityName) {
      super(null, Command.MODE.name(), null, entityName);
      this.entityName = entityName;
      this.modeString = null;
      this.parameters = null;
   }

   /**
    * Complete MODE Packet constructor.
    *
    * @param entityName the entity of the Mode packet i.e. a Channel name or
    *                   a User name
    * @param modeString a String containing mode codes/changes, eg. +o
    * @param parameters additional parameters for the mode
    */
   public ModeIrcPacket(final String entityName, final String modeString, final String... parameters) {
      super(null, Command.MODE.name(), null, mergeArguments(entityName, modeString, parameters));
      this.entityName = entityName;
      this.modeString = modeString;
      this.parameters = parameters;
   }

   /**
    * @return this Mode packet Entity name
    */
   public String getEntityName() {
      return this.entityName;
   }

   /**
    * @return this Mode packet Mode String
    */
   public String getModeString() {
      return modeString;
   }

   /**
    * @return this Mode packet parameters
    */
   public String[] getModeParameters() {
      return parameters;
   }
}
