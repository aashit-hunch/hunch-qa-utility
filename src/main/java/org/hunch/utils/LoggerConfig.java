package org.hunch.utils;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hunch.constants.GlobalData;

/**
 * Utility class to configure Log4j logging level dynamically based on GlobalData.ENABLE_INFO_LOGS flag
 *
 * This class should be called early in the application lifecycle (e.g., in main method or static initializer)
 * to set the appropriate logging level for all loggers.
 *
 * When ENABLE_INFO_LOGS is false:
 *   - INFO level logs are suppressed
 *   - Only WARN and ERROR logs are shown
 *
 * When ENABLE_INFO_LOGS is true:
 *   - All log levels (INFO, WARN, ERROR) are shown as per log4j.properties configuration
 */
public final class LoggerConfig {

    private static boolean isConfigured = false;

    private LoggerConfig() {
        // Private constructor to prevent instantiation
    }

    /**
     * Configures the Log4j root logger level based on GlobalData.ENABLE_INFO_LOGS flag
     * This method is idempotent - it will only configure once even if called multiple times
     */
    public static void configure() {
        if (isConfigured) {
            return;
        }

        Logger rootLogger = Logger.getRootLogger();

        if (GlobalData.ENABLE_INFO_LOGS) {
            // Keep INFO level as configured in log4j.properties
            rootLogger.setLevel(Level.INFO);
            System.out.println("[LoggerConfig] INFO level logging is ENABLED");
        } else {
            // Suppress INFO logs, only show WARN and ERROR
            rootLogger.setLevel(Level.WARN);
            System.out.println("[LoggerConfig] INFO level logging is DISABLED (only WARN and ERROR will be shown)");
        }

        isConfigured = true;
    }

    /**
     * Resets the configuration flag (useful for testing)
     */
    static void reset() {
        isConfigured = false;
    }
}
