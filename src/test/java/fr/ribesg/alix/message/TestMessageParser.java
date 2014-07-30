/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.message;

import fr.ribesg.alix.api.enums.Command;
import fr.ribesg.alix.api.enums.Reply;
import fr.ribesg.alix.api.message.IrcPacket;
import fr.ribesg.alix.api.message.PongIrcPacket;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TestMessageParser {

   private final IrcPacket awaitedIrcPacket;
   private final String    ircPacketString;

   public TestMessageParser(final IrcPacket awaitedIrcPacket, final String ircPacketString) {
      this.awaitedIrcPacket = awaitedIrcPacket;
      this.ircPacketString = ircPacketString;
   }

   @Test
   public void testParseMessage() {
      final IrcPacket ircPacket = IrcPacket.parseMessage(this.ircPacketString);
      Assert.assertEquals("Prefix doesn't match", awaitedIrcPacket.getPrefix(), ircPacket.getPrefix());
      Assert.assertEquals("Command doesn't match", awaitedIrcPacket.getRawCommandString(), ircPacket.getRawCommandString());
      Assert.assertEquals("Trail doesn't match", awaitedIrcPacket.getTrail(), ircPacket.getTrail());
      Assert.assertArrayEquals("Parameters doesn't match", awaitedIrcPacket.getParameters(), ircPacket.getParameters());
   }

   @Parameters
   public static Collection<Object[]> data() {
      final Collection<Object[]> data = new ArrayList<>();
      data.add(new Object[] {
         new PongIrcPacket("Test"),
         "PONG :Test"
      });
      data.add(new Object[] {
         new IrcPacket("DSH105!~DSH@2607:5300:60:2464::1", Command.PRIVMSG.name(), "sgtcaze :o?", "#drtshock"),
         ":DSH105!~DSH@2607:5300:60:2464::1 PRIVMSG #drtshock :sgtcaze :o?"
      });
      data.add(new Object[] {
         new IrcPacket("Ribesg", Command.USER.name(), "Ribesg", "Ribesg", "0", "*"),
         ":Ribesg USER Ribesg 0 * :Ribesg"
      });
      data.add(new Object[] {
         new IrcPacket("irc.xxxx.fr", Reply.RPL_NAMREPLY.getIntCodeAsString(), "BooBot @boozaa", "BooBot", "=", "#boozaa"),
         ":irc.xxxx.fr 353 BooBot =   #boozaa :BooBot @boozaa"
      });
      return data;
   }
}