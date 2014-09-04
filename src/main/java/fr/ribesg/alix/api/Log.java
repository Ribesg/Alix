/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.api;

import fr.ribesg.alix.api.bot.util.PasteUtil;
import fr.ribesg.alix.api.bot.util.WebUtil;
import fr.ribesg.alix.api.enums.Codes;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Logging class.
 * TODO Javadocs
 *
 * @author Ribesg
 */
public class Log {

   // ################# //
   // ## Back Logger ## //
   // ################# //

   private static final Logger logger = Logger.getLogger("Alix");

   public static Logger get() {
      return Log.logger;
   }

   // ############# //
   // ## Filters ## //
   // ############# //

   private static final Map<String, String> filters = new HashMap<>();

   public static void addFilter(final String regex, final String replacement) {
      Log.filters.put(regex, replacement);
   }

   private static String filter(final String message) {
      String result = message;
      for (final Entry<String, String> e : Log.filters.entrySet()) {
         result = result.replaceAll(e.getKey(), e.getValue());
      }
      return result;
   }

   // ################# //
   // ## Log Channel ## //
   // ################# //

   private static Channel logChannel;
   private static Priority logChannelLevel;

   public static Channel getLogChannel() {
      return Log.logChannel;
   }

   public static void setLogChannel(Channel logChannel) {
      Log.logChannel = logChannel;
   }

   public static Priority getLogChannelLevel() {
      return Log.logChannelLevel;
   }

   public static void setLogChannelLevel(final Priority logChannelLevel) {
      Log.logChannelLevel = logChannelLevel;
   }

   public static boolean isLogChannel(final Priority level) {
      return level.toInt() >= Log.logChannelLevel.toInt();
   }

   public static void logChannel(final Priority level, final String message) {
      Log.logChannel(level, message, false);
   }

   private static void logChannel(final Priority level, final String message, final boolean force) {
      if (Log.logChannel != null && (force || Log.isLogChannel(level))) {
         Log.logChannel.sendMessage(Codes.RED + '[' + level.toString() + "] " + message);
      }
   }

   // ################## //
   // ## Paste Errors ## //
   // ################## //

   private static boolean pasteErrors = false;

   public static boolean getPasteErrors() {
      return Log.pasteErrors;
   }

   public static void setPasteErrors(final boolean value) {
      Log.pasteErrors = value;
   }

   private static void paste(final Priority level, final String message, final Throwable t) {
      final StringBuilder builder = new StringBuilder();
      builder.append(message).append("\n\n");

      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      final PrintStream printOut = new PrintStream(out);
      t.printStackTrace(printOut);
      builder.append(out.toString());

      final String longLink = PasteUtil.paste(builder.toString());
      String link;
      try {
         link = WebUtil.shortenUrl(longLink);
      } catch (final IOException e) {
         link = longLink;
      }
      Log.logChannel(level, message + " (" + link + ')', true);
   }

   // ##################### //
   // ## Logging methods ## //
   // ##################### //

   public static boolean isEnabledFor(final Priority level) {
      return Log.logger.isEnabledFor(level);
   }

   public static void warn(final String message) {
      final String filtered = filter(message);
      Log.logger.warn(filtered);
      Log.logChannel(Level.WARN, filtered);
   }

   public static void warn(final String message, final Throwable t) {
      final String filtered = filter(message);
      Log.logger.warn(filtered, t);
      Log.paste(Level.WARN, filtered, t);
   }

   public static void log(final String callerFQCN, final Priority level, final String message, final Throwable t) {
      final String filtered = filter(message);
      Log.logger.log(callerFQCN, level, filtered, t);
      Log.paste(level, filtered, t);
   }

   public static void log(final Priority level, final String message) {
      final String filtered = filter(message);
      Log.logger.log(level, filtered);
      Log.logChannel(level, filtered);
   }

   public static void log(final Priority level, final String message, final Throwable t) {
      final String filtered = filter(message);
      Log.logger.log(level, filtered, t);
      Log.paste(level, filtered, t);
   }

   public static void info(final String message, final Throwable t) {
      final String filtered = filter(message);
      Log.logger.info(filtered, t);
      Log.paste(Level.INFO, filtered, t);
   }

   public static void info(final String message) {
      final String filtered = filter(message);
      Log.logger.info(filtered);
      Log.logChannel(Level.INFO, filtered);
   }

   public static void fatal(final String message) {
      final String filtered = filter(message);
      Log.logger.fatal(filtered);
      Log.logChannel(Level.FATAL, filtered);
   }

   public static void fatal(final String message, final Throwable t) {
      final String filtered = filter(message);
      Log.logger.fatal(filtered, t);
      Log.paste(Level.FATAL, filtered, t);
   }

   public static void error(final String message, final Throwable t) {
      final String filtered = filter(message);
      Log.logger.error(filtered, t);
      Log.paste(Level.ERROR, filtered, t);
   }

   public static void error(final String message) {
      final String filtered = filter(message);
      Log.logger.error(filtered);
      Log.logChannel(Level.ERROR, filtered);
   }

   public static void debug(final String message, final Throwable t) {
      final String filtered = filter(message);
      Log.logger.debug(filtered, t);
      Log.paste(Level.DEBUG, filtered, t);
   }

   public static void debug(final String message) {
      final String filtered = filter(message);
      Log.logger.debug(filtered);
      Log.logChannel(Level.DEBUG, filtered);
   }

   public static void assertLog(boolean assertion, final String msg) {
      final String filtered = filter(msg);
      Log.logger.assertLog(assertion, msg);
      if (assertion) {
         Log.logChannel(Level.ERROR, filtered);
      }
   }
}
