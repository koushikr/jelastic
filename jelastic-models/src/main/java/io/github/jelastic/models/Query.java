package io.github.jelastic.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import io.github.jelastic.models.filter.Filter;
import io.github.jelastic.models.paged.PageWindow;
import io.github.jelastic.models.sorter.Sorter;
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
