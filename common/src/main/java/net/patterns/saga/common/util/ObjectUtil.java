package net.patterns.saga.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class ObjectUtil {

    private ObjectUtil(){}

    public static <T> Optional<T> toObject(byte[] src, Class<T> type) {
        try {
            return Optional.of(new ObjectMapper().readValue(src, type));
        } catch (Exception e) {
            log.warn("toObject failed for type: {}",type,e);
            return Optional.empty();
        }
    }

    public static byte[] toBytes(Object o) {
        try {
            return new ObjectMapper().writeValueAsBytes(o);
        } catch (Exception e) {
            log.warn("toBytes failed for object: {}",o,e);
            return new byte[0];
        }
    }

}
