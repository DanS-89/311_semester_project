package service;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for the logging of application messages.
 * Ensures consistent message formats and logging
 */
public class MyLogger {

    //Static logger instance
    private final static Logger LOGGER =
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Logs messages at the info level, provides a standard prefix format
     * @param msg Message to be logged
     */
    public static void makeLog(String msg)
    {
        LOGGER.log(Level.INFO, "CSC311_Log__ "+msg);

    }
}
