/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.api.bot.config;
import fr.ribesg.alix.api.Channel;
import fr.ribesg.alix.api.Client;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.bot.util.configuration.YamlDocument;
import fr.ribesg.alix.api.bot.util.configuration.YamlFile;
import fr.ribesg.alix.api.network.ssl.SSLType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ribesg
 */
public class AlixConfiguration {

   public static AlixConfiguration getDefault() {
      return AlixConfiguration.getDefault("alix.yml");
   }

   public static AlixConfiguration getDefault(final String fileName) {
      final AlixConfiguration result = new AlixConfiguration(fileName);
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

   private final String   fileName;
   private final YamlFile file;

   protected final List<Server> servers;
   protected       String       mainNick;

   public AlixConfiguration() {
      this("alix.yml");
   }

   public AlixConfiguration(final String fileName) {
      this.fileName = fileName;
      this.file = new YamlFile();
      this.servers = new ArrayList<>();
   }

   public boolean exists() {
      return Files.exists(Paths.get(this.fileName));
   }

   public boolean load(final Client client) throws IOException {
      if (this.exists()) {
         this.file.load(this.fileName);
         final YamlDocument firstDocument = this.file.getDocuments().get(0);
         this.mainNick = firstDocument.getString("mainNick");
         this.loadMainAdditional(firstDocument);
         for (int i = 1; i < this.file.getDocuments().size(); i++) {
            final YamlDocument document = this.file.getDocuments().get(i);
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
            final Server server = new Server(client, clientNick, url, port, sslType);
            channels.forEach(server::addChannel);
            this.servers.add(server);
            this.loadServerAdditional(server, document);
         }
         return true;
      } else {
         this.newConfig();
         return false;
      }
   }

   protected void loadMainAdditional(final YamlDocument mainDocument) {
      // NOP
   }

   protected void loadServerAdditional(final Server server, final YamlDocument mainDocument) {
      // NOP
   }

   public void newConfig() throws IOException {
      final AlixConfiguration config = AlixConfiguration.getDefault(this.fileName);
      this.mainNick = config.mainNick;
      this.servers.addAll(config.servers);
      this.save();
   }

   public void save() throws IOException {
      this.file.getDocuments().clear();
      final YamlDocument firstDocument = new YamlDocument();
      firstDocument.set("mainNick", this.mainNick);
      this.saveMainAdditional(firstDocument);
      this.file.getDocuments().add(firstDocument);

      for (final Server server : this.servers) {
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
         if (!clientNick.equals(this.mainNick)) {
            document.set("clientNick", clientNick);
         }
         this.saveServerAdditional(server, document);
         this.file.getDocuments().add(document);
      }

      this.file.save(this.fileName);
   }

   protected void saveMainAdditional(final YamlDocument mainDocument) {
      // NOP
   }

   protected void saveServerAdditional(final Server server, final YamlDocument serverDocument) {
      // NOP
   }

   public List<Server> getServers() {
      return servers;
   }

   public String getMainNick() {
      return mainNick;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (!(o instanceof AlixConfiguration)) {
         return false;
      }

      AlixConfiguration that = (AlixConfiguration) o;

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
      int result = this.servers.hashCode();
      result = 31 * result + (this.mainNick != null ? this.mainNick.hashCode() : 0);
      return result;
   }
}
