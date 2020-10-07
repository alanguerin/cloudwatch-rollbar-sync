package com.alanguerin.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides convenient access to a class logger.
 */
public interface Loggable {
    
    default Logger getLogger() {
        return LogManager.getLogger(getClass());
    }
    
}
