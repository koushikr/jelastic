package io.github.jelastic.core.models.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import io.github.jelastic.core.models.query.filter.Filter;
import io.github.jelastic.core.models.query.paged.PageWindow;
import io.github.jelastic.core.models.query.sorter.JElasticSorter;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JElasticQuery {

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
    private Set<JElasticSorter> sorters = new TreeSet();



    public void addFilter(Filter filter) {
        if (Objects.isNull(filters) || filters.isEmpty()) {
            this.filters = Sets.newHashSet(filter);
        } else {
            this.filters.add(filter);
        }
    }

    public void addJElasticSorter(JElasticSorter sorter) {
        if (Objects.isNull(sorters) || sorters.isEmpty()) {
            this.sorters = Sets.newTreeSet();
        }

        this.sorters.add(sorter);
    }
}
