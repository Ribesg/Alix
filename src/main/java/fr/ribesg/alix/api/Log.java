/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.api;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Logging class.
 *
 * @author Ribesg
 */
public class Log {

   private static final Logger logger = Logger.getRootLogger();

   private static final Map<String, String> filters = new HashMap<>();

   private static String filter(final String message) {
      String result = message;
      for (final Entry<String, String> e : Log.filters.entrySet()) {
         result = result.replaceAll(e.getKey(), e.getValue());
      }
      return result;
   }

   public static void warn(String message) {
      logger.warn(filter(message));
   }

   public static void warn(String message, Throwable t) {
      logger.warn(filter(message), t);
   }

   public static void log(String callerFQCN, Priority level, String message, Throwable t) {
      logger.log(callerFQCN, level, filter(message), t);
   }

   public static void log(Level level, String message) {
      logger.log(level, filter(message));
   }

   public static void log(Level level, String message, Throwable t) {
      logger.log(level, filter(message), t);
   }

   public static boolean isDebugEnabled() {
      return logger.isDebugEnabled();
   }

   public static boolean isEnabledFor(Priority level) {
      return logger.isEnabledFor(level);
   }

   public static boolean isInfoEnabled() {
      return logger.isInfoEnabled();
   }

   public static void info(String message, Throwable t) {
      logger.info(filter(message), t);
   }

   public static void info(String message) {
      logger.info(filter(message));
   }

   public static void fatal(String message) {
      logger.fatal(filter(message));
   }

   public static void fatal(String message, Throwable t) {
      logger.fatal(filter(message), t);
   }

   public static void error(String message, Throwable t) {
      logger.error(filter(message), t);
   }

   public static void error(String message) {
      logger.error(filter(message));
   }

   public static void debug(String message, Throwable t) {
      logger.debug(filter(message), t);
   }

   public static void debug(String message) {
      logger.debug(filter(message));
   }

   public static void assertLog(boolean assertion, String msg) {
      logger.assertLog(assertion, msg);
   }

   public static void addFilter(final String regex, final String replacement) {
      Log.filters.put(regex, replacement);
   }

   public static Logger get() {
      return Log.logger;
   }
}
