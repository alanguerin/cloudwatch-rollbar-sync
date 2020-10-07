package com.alanguerin.util;

import com.alanguerin.logging.Loggable;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.isNull;

public class GZipDecompressor implements Function<byte[], String>, Loggable {

    /**
     * Decompresses a GZip compressed byte array.
     * @return a decompressed UTF-8 string.
     */
    @Override
    public String apply(byte[] compressed) {
        if (isNull(compressed) || compressed.length == 0) {
            return null;
        }
        
        try (
            ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
            GZIPInputStream gis = new GZIPInputStream(bis);
            BufferedReader reader = new BufferedReader(new InputStreamReader(gis, UTF_8))
        ) {
            StringBuilder sb = new StringBuilder();
            String line;
            
            while((line = reader.readLine()) != null) {
                sb.append(line);
            }
            
            return sb.toString();
        } catch (IOException e) {
            getLogger().error("Unexpected exception.", e);
            throw new RuntimeException(e);
        }
    }
    
}
