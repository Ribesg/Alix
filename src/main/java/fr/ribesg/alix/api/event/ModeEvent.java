package fr.ribesg.alix.api.event;

import fr.ribesg.alix.api.Channel;
import fr.ribesg.alix.api.Source;

/**
 * Thrown when the Client receives a MODE command.
 *
 * @author Ribesg
 */
public class ModeEvent extends Event {

   private final Source   source;
   private final Channel  channel;
   private final String   modeString;
   private final String[] parameters;

   public ModeEvent(final Source source, final Channel channel, final String modeString, final String... parameters) {
      this.source = source;
      this.channel = channel;
      this.modeString = modeString;
      this.parameters = parameters;
   }

   public Source getSource() {
      return this.source;
   }

   public Channel getChannel() {
      return this.channel;
   }

   public String getModeString() {
      return this.modeString;
   }

   public boolean hasParameters() {
      return this.parameters != null && this.parameters.length > 0;
   }

   public String[] getParameters() {
      return this.parameters;
   }
}
