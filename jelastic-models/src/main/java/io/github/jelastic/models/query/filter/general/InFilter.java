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
package io.github.jelastic.models.query.filter.general;

import io.github.jelastic.models.query.filter.Filter;
import io.github.jelastic.models.query.filter.FilterType;
import io.github.jelastic.models.query.filter.FilterVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * Created by koushikr
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InFilter extends Filter {

    private List<Object> values;

    public InFilter() {
        super(FilterType.IN);
    }

    public InFilter(String field, List<Object> values) {
        super(FilterType.IN, field);
        this.values = values;
    }

    @Override
    public <V> V accept(FilterVisitor<V> visitor) {
        return visitor.visit(this);
    }
}
