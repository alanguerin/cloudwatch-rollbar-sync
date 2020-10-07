package com.alanguerin.domain;

import com.rollbar.api.payload.data.Level;
import lombok.Getter;

import java.util.List;

/**
 * Defines aliases for Rollbar log {@link Level}s.
 */
@Getter
public enum LogLevelAlias {
    
    CRITICAL(Level.CRITICAL, List.of("CRITICAL", "FATAL", "SEVERE")),
    ERROR(Level.ERROR, List.of("ERROR")),
    WARNING(Level.WARNING, List.of("WARNING", "WARN")),
    INFO(Level.INFO, List.of("INFO")),
    DEBUG(Level.DEBUG, List.of("DEBUG", "TRACE"));
    
    /**
     * Rollbar log level.
     */
    private Level level;

    /**
     * Aliases for a given log level..
     */
    private List<String> aliases;
    
    LogLevelAlias(Level level, List<String> aliases) {
        this.level = level;
        this.aliases = aliases;
    }
    
}
