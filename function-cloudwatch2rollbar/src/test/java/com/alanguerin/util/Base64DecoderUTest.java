package com.alanguerin.util;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.Charset;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.UK;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class Base64DecoderUTest {
    
    private Base64Decoder decoder;

    @BeforeEach
    public void beforeEach() {
        decoder = new Base64Decoder();
    }

    /**
     * Tests for {@link Base64Decoder#apply(byte[])} method.
     */
    @Nested
    class ApplyTests {
        
        @NullAndEmptySource
        @ParameterizedTest
        public void testApply_whenEncodedStringIsNullOrEmpty_expectEmptyResult(byte[] encoded) {
            byte[] result = decoder.apply(encoded);
            
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }

        @Test
        public void testApply_withUTF8EncodedString_expectSuccessfulResult() {
            String message = Faker.instance(UK).backToTheFuture().quote();
            String utf8Message = new String(message.getBytes(), UTF_8);
            
            byte[] encoded = Base64.getEncoder().encode(utf8Message.getBytes());
            byte[] decoded = decoder.apply(encoded);
            
            Base64.Decoder base64Decoder = Base64.getDecoder();
            assertThat(decoded).isEqualTo(base64Decoder.decode(encoded));
        }

        @ParameterizedTest
        @ValueSource(strings = { "UTF-8", "UTF-16", "UTF-16LE", "UTF-16BE", "ASCII", "ISO_8859_1" })
        public void testApply_withDifferentEncodings_expectSuccessfulResult(String encoding) {
            String message = Faker.instance(UK).backToTheFuture().quote();
            
            String encodedMessage = new String(message.getBytes(), Charset.forName(encoding));
            System.out.println(encoding + " encoded message: " + encodedMessage);
            
            byte[] encoded = Base64.getEncoder().encode(encodedMessage.getBytes());
            byte[] decoded = decoder.apply(encoded);
            
            Base64.Decoder base64Decoder = Base64.getDecoder();
            assertThat(decoded).isEqualTo(base64Decoder.decode(encoded));
        }
        
    }
    
}
