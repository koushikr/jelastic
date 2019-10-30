/*
 * Copyright 2019 Koushik R <rkoushik.14@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.jelastic.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import io.github.jelastic.core.exception.JelasticException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author koushik
 */
@Slf4j
public class MapperUtils {


    private static ObjectMapper mapper;

    public static void init(ObjectMapper objectMapper) {
        mapper = objectMapper;
    }

    public static ObjectMapper mapper() {
        Preconditions.checkNotNull(mapper, "Please call MapperUtils.init(mapper) to set mapper");
        return mapper;
    }

    public static <T> String writeValueAsString(T t) {
        Preconditions.checkNotNull(mapper, "Please call MapperUtils.init(mapper) to set mapper");
        try {
            return mapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException while writing object ", e);
            throw new JelasticException("JsonProcessingException in method writeValueAsString");
        }
    }
}
