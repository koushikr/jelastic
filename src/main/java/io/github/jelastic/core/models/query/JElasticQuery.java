package io.github.jelastic.core.models.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.github.jelastic.core.models.query.filter.Filter;
import io.github.jelastic.core.models.query.paged.PageWindow;
import io.github.jelastic.core.models.query.sorter.JElasticSorter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.elasticsearch.search.rescore.RescorerBuilder;

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
    @Builder.Default
    private List<RescorerBuilder> reScorers = new ArrayList<>();

    private String[] includedSourceField;

    private String[] excludedSourceField;


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

    public void addRescorer(RescorerBuilder rescorerBuilder) {
        if (Objects.isNull(reScorers) || reScorers.isEmpty()) {
            this.reScorers = Lists.newArrayList(rescorerBuilder);
        }
        this.reScorers.add(rescorerBuilder);
    }
}
