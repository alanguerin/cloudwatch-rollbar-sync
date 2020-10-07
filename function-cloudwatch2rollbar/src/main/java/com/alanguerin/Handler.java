package com.alanguerin;

import com.alanguerin.domain.LogEventRequest;
import com.alanguerin.logging.Loggable;
import com.alanguerin.system.Environmentable;
import com.alanguerin.util.Base64Decoder;
import com.alanguerin.util.GZipDecompressor;
import com.alanguerin.util.LogLevelEvaluator;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.CloudWatchLogsEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rollbar.api.payload.data.Level;
import com.rollbar.notifier.Rollbar;
import com.rollbar.notifier.config.Config;
import com.rollbar.notifier.config.ConfigBuilder;
import lombok.Getter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static com.rollbar.api.payload.data.Level.INFO;
import static com.rollbar.api.payload.data.Level.WARNING;

@Getter
public class Handler implements RequestHandler<CloudWatchLogsEvent, Void>, Loggable, Environmentable {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Base64Decoder base64Decoder = new Base64Decoder();
    private final GZipDecompressor gzipDecompressor = new GZipDecompressor();
    private final LogLevelEvaluator levelEvaluator = new LogLevelEvaluator();
    
    @Override
    public Void handleRequest(CloudWatchLogsEvent request, Context context) {
        getLogger().debug("Received: " + request);
        
        String data = Optional.ofNullable(request)
            .map(CloudWatchLogsEvent::getAwsLogs)
            .map(CloudWatchLogsEvent.AWSLogs::getData)
            .orElseThrow(() -> new IllegalArgumentException("Request is missing required attributes."));
        
        Optional<String> logEvent = Optional.ofNullable(data)
            .map(String::getBytes)
            .map(getBase64Decoder())
            .map(getGzipDecompressor());
        
        logEvent.ifPresent(json -> {
            try {
                LogEventRequest eventRequest = toLogEventRequest(json);
                
                Config config = ConfigBuilder.withAccessToken(getEnv("ROLLBAR_ACCESS_TOKEN"))
                    .environment(getEnv("ROLLBAR_ENVIRONMENT"))
                    .language(getEnv("ROLLBAR_LANGUAGE"))
                    .defaultMessageLevel(INFO)
                    .defaultErrorLevel(WARNING)
                    .enabled(true)
                    .build();
                    
                    Rollbar rollbar = new Rollbar(config);
                    
                    Map<String, Object> custom = Map.of(
                        "awsRequestId", context.getAwsRequestId(),
                        "logGroup", eventRequest.getLogGroup(),
                        "logStream", eventRequest.getLogStream()
                    );
                    
                    eventRequest.getEvents().forEach(event -> {
                        Level level = Optional.ofNullable(event.getMessage())
                            .map(getLevelEvaluator())
                            .orElse(INFO);
                        
                        if (!isAllowed(level)) {
                            getLogger().debug("Log message filtered out. [level={}]", level.toString());
                            return; // Skip this log event.
                        }

                        rollbar.log(event.getMessage(), custom, level);
                    });

                    rollbar.close(true);
            } catch (Exception e) {
                getLogger().error("Unexpected exception.", e);
                throw new RuntimeException(e);
            }
        });
            
        return null;
    }

    /**
     * Deserialize a json payload into a {@link LogEventRequest} object.
     */
    private LogEventRequest toLogEventRequest(String payload) {
        try {
            return getObjectMapper().readValue(payload, new TypeReference<>() {});
        } catch (IOException e) {
            getLogger().error("Unexpected exception.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Determine if the provided log level should be filtered out.
     */
    private boolean isAllowed(Level level) {
        return Optional.ofNullable(getEnv("ROLLBAR_FILTER_THRESHOLD"))
            .map(Level::lookupByName)
            .filter(threshold -> level.level() >= threshold.level())
            .isPresent();
    }
    
    private boolean isFiltered(Level level) {
        return Optional.ofNullable(getEnv("ROLLBAR_FILTER_THRESHOLD"))
            .map(Level::lookupByName)
            .filter(threshold -> level.level() >= threshold.level())
            .isPresent();
    }
    
}