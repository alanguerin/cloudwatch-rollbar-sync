package com.alanguerin.util;

import java.util.Base64;
import java.util.Optional;
import java.util.function.Function;

public class Base64Decoder implements Function<byte[], byte[]> {

    @Override
    public byte[] apply(byte[] encoded) {
        return Optional.ofNullable(encoded)
            .map(s -> Base64.getDecoder().decode(s))
            .orElse(new byte[0]);
    }
    
}
