package com.alanguerin.system;

/**
 * Provides convenient access to system environment variables.
 */
public interface Environmentable {
    
    /**
     * Get an environment variable.
     * @return the value of the environment variable, or null if it cannot be found or accessed.
     */
    default String getEnv(String name) {
        try {
            return System.getenv(name);
        } catch (NullPointerException | SecurityException e) {
            return null;
        }
    }
    
}
