/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 *
 * Project file:    Alix - Alix - BotConfiguration.java
 * Full Class name: fr.ribesg.alix.api.bot.config.BotConfiguration
 */

package fr.ribesg.alix.api.bot.config;
import fr.ribesg.alix.api.Channel;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.bot.util.configuration.YamlDocument;
import fr.ribesg.alix.api.bot.util.configuration.YamlFile;
import fr.ribesg.alix.api.network.ssl.SSLType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ribesg
 */
public class BotConfiguration {

   public static BotConfiguration getDefault() {
      final BotConfiguration result = new BotConfiguration();
      result.mainNick = "AlixTestBot";
      result.servers.add(new Server(null, "irc.test.net", 6667) {{
         addChannel("#channelOne");
         addChannel("#channelTwo");
      }});
      result.servers.add(new Server(null, "AlixTestBot", "irc.someServer.net", 1337, SSLType.TRUSTING) {{
         addChannel("#channelOne");
         addChannel("#channelTwo");
      }});
      result.servers.add(new Server(null, "AlixTestBot_", "irc.someOtherServer.net", 6697, SSLType.SECURED) {{
         addChannel("#channelOne");
         addChannel("#channelTwo");
      }});
      return result;
   }

   private final YamlFile file;

   private final List<Server> servers;

   private String mainNick;

   public BotConfiguration() {
      this.file = new YamlFile();
      this.servers = new ArrayList<>();
   }

   public void load() throws IOException {
      file.load();

      final YamlDocument firstDocument = file.getDocuments().get(0);
      this.mainNick = firstDocument.getString("mainNick");

      for (int i = 1; i < file.getDocuments().size(); i++) {
         final YamlDocument document = file.getDocuments().get(i);
         final String url = document.getString("url");
         final int port = document.getInt("port");
         final SSLType sslType;
         if (document.isString("ssl")) {
            sslType = SSLType.valueOf(document.getString("ssl"));
         } else {
            sslType = SSLType.NONE;
         }
         final List<String> channels = document.getStringList("channels");
         final String clientNick;
         if (document.isString("clientNick")) {
            clientNick = document.getString("clientNick");
         } else {
            clientNick = this.mainNick;
         }
         final Server server = new Server(null, clientNick, url, port, sslType);
         channels.forEach(server::addChannel);
         this.servers.add(server);
      }
   }

   public void save() throws IOException {
      final YamlDocument firstDocument = new YamlDocument();
      firstDocument.set("mainNick", this.mainNick);
      file.getDocuments().add(firstDocument);

      for (final Server server : servers) {
         final YamlDocument document = new YamlDocument();

         final String url = server.getUrl();
         final int port = server.getPort();
         final SSLType sslType = server.getSslType();
         final List<String> channels = server.getChannels().stream().map(Channel::getName).collect(Collectors.toList());
         final String clientNick = server.getClientNick();

         document.set("url", url);
         document.set("port", port);
         if (sslType != SSLType.NONE) {
            document.set("ssl", sslType.name());
         }
         document.set("channels", channels);
         if (!clientNick.equals(mainNick)) {
            document.set("clientNick", clientNick);
         }

         file.getDocuments().add(document);
      }

      file.save();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (!(o instanceof BotConfiguration)) {
         return false;
      }

      BotConfiguration that = (BotConfiguration) o;

      if (mainNick != null ? !mainNick.equals(that.mainNick) : that.mainNick != null) {
         return false;
      }
      if (!servers.equals(that.servers)) {
         return false;
      }

      return true;
   }

   @Override
   public int hashCode() {
      int result = servers.hashCode();
      result = 31 * result + (mainNick != null ? mainNick.hashCode() : 0);
      return result;
   }
}
