package fr.ribesg.alix.api;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/** @author Ribesg */
public class Log {

	private static final Logger logger = Logger.getRootLogger();

	public static void warn(Object message) {
		logger.warn(message);
	}

	public static void warn(Object message, Throwable t) {
		logger.warn(message, t);
	}

	public static void log(String callerFQCN, Priority level, Object message, Throwable t) {
		logger.log(callerFQCN, level, message, t);
	}

	public static void log(Level level, Object message) {
		logger.log(level, message);
	}

	public static void log(Level level, Object message, Throwable t) {
		logger.log(level, message, t);
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

	public static void info(Object message, Throwable t) {
		logger.info(message, t);
	}

	public static void info(Object message) {
		logger.info(message);
	}

	public static void fatal(Object message) {
		logger.fatal(message);
	}

	public static void fatal(Object message, Throwable t) {
		logger.fatal(message, t);
	}

	public static void error(Object message, Throwable t) {
		logger.error(message, t);
	}

	public static void error(Object message) {
		logger.error(message);
	}

	public static void debug(Object message, Throwable t) {
		logger.debug(message, t);
	}

	public static void debug(Object message) {
		logger.debug(message);
	}

	public static void assertLog(boolean assertion, String msg) {
		logger.assertLog(assertion, msg);
	}
}
