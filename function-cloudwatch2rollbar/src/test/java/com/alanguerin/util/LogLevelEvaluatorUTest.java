package com.alanguerin.util;

import com.rollbar.api.payload.data.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.rollbar.api.payload.data.Level.ERROR;
import static com.rollbar.api.payload.data.Level.WARNING;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class LogLevelEvaluatorUTest {
    
    private LogLevelEvaluator evaluator;

    @BeforeEach
    public void beforeEach() {
        evaluator = new LogLevelEvaluator();
    }

    /**
     * Tests for {@link LogLevelEvaluator#apply(String) method.}
     */
    @Nested
    class ApplyTests {
        
        @NullAndEmptySource
        @ParameterizedTest
        public void testApply_whenMessageIsNullOrEmpty_expectNullResult(String message) {
            Level result = evaluator.apply(message);
            
            assertThat(result).isNull();
        }
        
        @ValueSource(strings = { "WARN", "WARNING", "Warn" })
        @ParameterizedTest
        public void testApply_whenMessageContainsOneLevel_expectWarningResult(String alias) {
            String message = String.format("%s %s %s %s - Hello world.",
                LocalDateTime.now().toString(), UUID.randomUUID(), alias, getClass().getCanonicalName());
            
            Level result = evaluator.apply(message);
            assertThat(result).isEqualTo(WARNING);
        }
        
        @ValueSource(strings = { "WARN", "WARNING", "Warn" })
        @ParameterizedTest
        public void testApply_whenMessageContainsLessImportantLevels_expectWarningResult(String alias) {
            String message = String.format("%s %s %s %s - INFO DEBUG Hello world.",
                LocalDateTime.now().toString(), UUID.randomUUID(), alias, getClass().getCanonicalName());

            Level result = evaluator.apply(message);
            assertThat(result).isEqualTo(WARNING);
        }
        
        @ValueSource(strings = { "WARN", "WARNING", "Warn" })
        @ParameterizedTest
        public void testApply_whenMessageContainsMoreImportantLevels_expectErrorResult(String alias) {
            String message = String.format("%s %s %s %s - ERROR Hello world.",
                LocalDateTime.now().toString(), UUID.randomUUID(), alias, getClass().getCanonicalName());
            
            Level result = evaluator.apply(message);
            assertThat(result).isEqualTo(ERROR);
        }
        
    }
    
}
