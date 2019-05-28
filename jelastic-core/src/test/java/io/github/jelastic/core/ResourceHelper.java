package io.github.jelastic.core;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author koushik
 */
public class ResourceHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T getResource(String path, Class<T> klass) throws IOException {
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        final InputStream data = ResourceHelper.class.getClassLoader().getResourceAsStream(path);
        return objectMapper.readValue(data, klass);
    }

}
