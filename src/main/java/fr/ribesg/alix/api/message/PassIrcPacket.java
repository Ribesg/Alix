package fr.ribesg.alix.api.message;

import fr.ribesg.alix.api.enums.Command;

public class PassIrcPacket extends IrcPacket {

   private final String password;

   public PassIrcPacket(final String password) {
      super(null, Command.PASS.name(), null, password);
      this.password = password;
   }

   /**
    * @return this Pass packet password
    */
   public String getPassword() {
      return this.password;
   }
}
