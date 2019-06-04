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
package io.github.jelastic.core.models.query.filter.predicate;


import io.github.jelastic.core.models.query.filter.Filter;
import io.github.jelastic.core.models.query.filter.FilterType;
import io.github.jelastic.core.models.query.filter.FilterVisitor;
import lombok.*;

import java.util.List;

/**
 * Created by koushikr
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ORFilter extends Filter {

    @Singular
    private List<Filter> filters;

    protected ORFilter() {
        super(FilterType.OR);
    }

    @Builder
    public ORFilter(@Singular List<Filter> filters) {
        super(FilterType.OR);
        this.filters = filters;
    }

    @Override
    public boolean validate() {
        return filters.stream().map(Filter::validate).reduce((x, y) -> x && y).orElse(false);
    }

    @Override
    public <V> V accept(FilterVisitor<V> visitor) {
        return visitor.visit(this);
    }
}
