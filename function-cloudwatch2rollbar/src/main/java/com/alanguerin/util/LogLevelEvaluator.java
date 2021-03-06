package com.alanguerin.util;

import com.alanguerin.domain.LogLevelAlias;
import com.alanguerin.logging.Loggable;
import com.rollbar.api.payload.data.Level;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

/**
 * This function evaluates the log level of the given message string. 
 */
public class LogLevelEvaluator implements Function<String, Level>, Loggable  {

    /**
     * Capture group must be a complete word, and may be surrounded by spaces and some special characters (no quotes or underscores).
     * e.g. Valid matches are: " ERROR ", and "-ERROR-". Invalid matches are: "ERROR123", "\"ERROR\"".
     */
    private static final String REGEX_FORMAT = "(?<!\\w|\\\"|\\')\\s*(%s)\\s*(?!\\w|\\\"|\\')";

    /**
     * If a message exceeds this length, it will be left trimmed accordingly.
     */
    private static final int MESSAGE_TRIM_LENGTH = 200;
    
    /**
     * Attempt to evaluate the message's log level based on its content.
     * Note, the below algorithm searches for case-sensitive matches.
     */
    @Override
    public Level apply(String message) {
        if (isNull(message) || message.isBlank()) {
            return null;
        }
        
        // Shorten the message if eligible.
        final String shortMessage = StringUtils.left(message, MESSAGE_TRIM_LENGTH);
        
        return Arrays.stream(LogLevelAlias.values())
            .sorted()
            .filter(lvl -> {
                String capturingGroup = String.join("|", lvl.getAliases());
                Pattern pattern = Pattern.compile(String.format(REGEX_FORMAT, capturingGroup), CASE_INSENSITIVE);
                
                Matcher matcher = pattern.matcher(shortMessage);
                return matcher.find();
            })
            .findFirst()
            .map(LogLevelAlias::getLevel)
            .orElse(null);
    }
    
}
