/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.api.bot.util.configuration;
import java.util.Map;

/**
 * @author Ribesg
 */
public class YamlDocument extends ConfigurationSection {

   public YamlDocument() {
      super();
   }

   public YamlDocument(final Map<String, Object> contentMap) {
      super(contentMap);
   }

   public Map<String, Object> asMap() {
      return this.contentMap;
   }
}
