package server.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggingUtil {
    private static final Logger logger = LogManager.getLogger(LoggingUtil.class);
    

    public static void debug(Class<?> clazz, String message) {
        Logger log = LogManager.getLogger(clazz);
        log.debug(message);
    }
    

    public static void debug(Class<?> clazz, String message, Object... params) {
        Logger log = LogManager.getLogger(clazz);
        log.debug(message, params);
    }
    
   
    public static void info(Class<?> clazz, String message) {
        Logger log = LogManager.getLogger(clazz);
        log.info(message);
    }
    

    public static void info(Class<?> clazz, String message, Object... params) {
        Logger log = LogManager.getLogger(clazz);
        log.info(message, params);
    }
    

    public static void warn(Class<?> clazz, String message) {
        Logger log = LogManager.getLogger(clazz);
        log.warn(message);
    }
    

    public static void warn(Class<?> clazz, String message, Object... params) {
        Logger log = LogManager.getLogger(clazz);
        log.warn(message, params);
    }
    
    public static void error(Class<?> clazz, String message) {
        Logger log = LogManager.getLogger(clazz);
        log.error(message);
    }
    

    public static void error(Class<?> clazz, String message, Throwable exception) {
        Logger log = LogManager.getLogger(clazz);
        log.error(message, exception);
    }
    
    public static void error(Class<?> clazz, String message, Throwable exception, Object... params) {
        Logger log = LogManager.getLogger(clazz);
        log.error(String.format(message, params), exception);
    }
}
