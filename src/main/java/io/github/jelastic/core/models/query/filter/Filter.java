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
package io.github.jelastic.core.models.query.filter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Strings;
import io.github.jelastic.core.models.query.filter.general.*;
import io.github.jelastic.core.models.query.filter.number.*;
import io.github.jelastic.core.models.query.filter.predicate.AndFilter;
import io.github.jelastic.core.models.query.filter.predicate.ORFilter;
import lombok.Data;
import lombok.SneakyThrows;

/**
 * Created by koushikr
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "filterType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GreaterEqualsFilter.class, name = FilterType.GREATER_EQUAL),
        @JsonSubTypes.Type(value = GreaterThanFilter.class, name = FilterType.GREATER_THAN),
        @JsonSubTypes.Type(value = LesserEqualsFilter.class, name = FilterType.LESS_EQUAL),
        @JsonSubTypes.Type(value = LesserThanFilter.class, name = FilterType.LESS_THAN),
        @JsonSubTypes.Type(value = BetweenFilter.class, name = FilterType.BETWEEN),
        @JsonSubTypes.Type(value = EqualsFilter.class, name = FilterType.EQUALS),
        @JsonSubTypes.Type(value = InFilter.class, name = FilterType.IN),
        @JsonSubTypes.Type(value = NotInFilter.class, name = FilterType.NOT_IN),
        @JsonSubTypes.Type(value = NotEqualsFilter.class, name = FilterType.NOT_EQUALS),
        @JsonSubTypes.Type(value = AnyFilter.class, name = FilterType.ANY),
        @JsonSubTypes.Type(value = ExistsFilter.class, name = FilterType.EXISTS),
        @JsonSubTypes.Type(value = MissingFilter.class, name = FilterType.MISSING),
        @JsonSubTypes.Type(value = ContainsFilter.class, name = FilterType.CONTAINS),
        @JsonSubTypes.Type(value = AndFilter.class, name = FilterType.AND),
        @JsonSubTypes.Type(value = ORFilter.class, name = FilterType.OR),
        @JsonSubTypes.Type(value = ConstantScoreFilter.class, name = FilterType.CONSTANT_SCORE),
        @JsonSubTypes.Type(value = MatchFilter.class, name = FilterType.MATCH),
        @JsonSubTypes.Type(value = MatchFilter.class, name = FilterType.NESTED)
})
@Data
public abstract class Filter {

    private final String filterType;

    private String fieldName;

    public Filter(String filterType) {
        this.filterType = filterType;
    }

    public Filter(String filterType, String fieldName) {
        this.filterType = filterType;
        this.fieldName = fieldName;
    }

    @SneakyThrows
    public boolean validate() {
        if (Strings.isNullOrEmpty(fieldName)) {
            throw new RuntimeException("INVALID_FILTER field cannot be empty");
        }
        return true;
    }

    /**
     * @param visitor visitor impl
     * @param <V>     type fo return
     * @return act on the visitor with itself (the subclass), and return what he returns
     * <p>
     * Usually I don't to visitors at all, for I don't like their obscurity. I'd rather do double
     * dispatches by interfaces and implementations but Query of catalogue seems a good place to do
     * it
     */
    public abstract <V> V accept(FilterVisitor<V> visitor);

}
