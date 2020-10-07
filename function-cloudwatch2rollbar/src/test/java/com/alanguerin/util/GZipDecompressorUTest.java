package com.alanguerin.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class GZipDecompressorUTest {
    
    private GZipDecompressor decompressor;

    @BeforeEach
    public void beforeEach() {
        decompressor = new GZipDecompressor();
    }

    /**
     * Tests for {@link GZipDecompressor#apply(byte[])} method.
     */
    @Nested
    class ApplyTests {
        
        @NullAndEmptySource
        @ParameterizedTest
        public void testApply_whenCompressedStringIsNullOrEmpty_expectEmptyResult(byte[] compressed) {
            String result = decompressor.apply(compressed);
            
            assertThat(result).isNull();
        }
        
    }
    
}
