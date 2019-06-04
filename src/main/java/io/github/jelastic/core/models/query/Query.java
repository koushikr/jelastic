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
package io.github.jelastic.core.models.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import io.github.jelastic.core.models.query.filter.Filter;
import io.github.jelastic.core.models.query.paged.PageWindow;
import io.github.jelastic.core.models.query.sorter.Sorter;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by koushikr
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Query {

    public static String RAW_QUERY_NAME = "rawQuery";
    @NonNull
    public PageWindow pageWindow;
    @Builder.Default
    private String queryName = "query";
    @JsonProperty
    @NonNull
    @Builder.Default
    private Set<Filter> filters = new HashSet();
    @JsonProperty
    @NonNull
    @Builder.Default
    private Set<Sorter> sorters = new TreeSet();

    public void addFilter(Filter filter) {
        if (Objects.isNull(filters) || filters.isEmpty()) {
            this.filters = Sets.newHashSet(filter);
        } else {
            this.filters.add(filter);
        }
    }

    public void addSorter(Sorter sorter) {
        if (Objects.isNull(sorters) || sorters.isEmpty()) {
            this.sorters = Sets.newTreeSet();
        }

        this.sorters.add(sorter);
    }

}
