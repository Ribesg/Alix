/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.api.bot.util.configuration;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author Ribesg
 */
public class ConfigurationTest {

   @Test
   public void testConfiguration() {
      final YamlFile file = new YamlFile();

      final YamlDocument document1 = new YamlDocument();
      document1.set("anInt", 42);
      document1.set("aString", "Hello wolrd!");

      final ConfigurationSection section11 = new ConfigurationSection();
      section11.set("aStringList", Arrays.asList("StringA", "StringB", "StringC", "StringD"));
      section11.set("anIntList", Arrays.asList(1, 2, 3, 4));

      final ConfigurationSection section12 = new ConfigurationSection();
      section12.set("anotherInt", -1);

      document1.set("aSection", section11);
      document1.set("anotherSection", section12);

      file.getDocuments().add(document1);

      final YamlDocument document2 = new YamlDocument();
      document2.set("someOtherString", "The sky is blue.");

      file.getDocuments().add(document2);

      final String output = file.saveToString();

      Assert.assertEquals("anInt: 42\n" +
                          "aString: Hello wolrd!\n" +
                          "aSection:\n" +
                          "  aStringList:\n" +
                          "  - StringA\n" +
                          "  - StringB\n" +
                          "  - StringC\n" +
                          "  - StringD\n" +
                          "  anIntList:\n" +
                          "  - 1\n" +
                          "  - 2\n" +
                          "  - 3\n" +
                          "  - 4\n" +
                          "anotherSection:\n" +
                          "  anotherInt: -1\n" +
                          "---\n" +
                          "someOtherString: The sky is blue.\n", output);

      final YamlFile theFile = new YamlFile();
      theFile.loadFromString(output);

      Assert.assertEquals(file, theFile);
   }
}
