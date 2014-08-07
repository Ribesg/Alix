/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.api.message;
import fr.ribesg.alix.api.enums.Command;

/**
 * This class allow easy build of a TOPIC IRC Packet.
 */
public class TopicIrcPacket extends IrcPacket {

   private final String channelName;
   private final String newTopic;

   /**
    * Just get TOPIC.
    *
    * @param channelName the Channel name
    */
   public TopicIrcPacket(final String channelName) {
      this(channelName, null);
   }

   /**
    * Change TOPIC.
    *
    * @param channelName the Channel name
    * @param newTopic    the new topic
    */
   public TopicIrcPacket(final String channelName, final String newTopic) {
      super(null, Command.TOPIC.name(), newTopic, channelName);
      this.channelName = channelName;
      this.newTopic = newTopic;
   }

   /**
    * @return this Topic packet Channel name
    */
   public String getChannelName() {
      return channelName;
   }

   /**
    * @return true if this Topic packet holds a new Topic, false otherwise
    */
   public boolean isNewTopic() {
      return this.newTopic != null;
   }

   /**
    * @return this Topic packet new Topic if any, null otherwise
    */
   public String getNewTopic() {
      return newTopic;
   }
}
