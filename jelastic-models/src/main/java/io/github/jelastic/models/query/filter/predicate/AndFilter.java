package io.github.jelastic.models.query.filter.predicate;

import io.github.jelastic.models.query.filter.Filter;
import io.github.jelastic.models.query.filter.FilterType;
import io.github.jelastic.models.query.filter.FilterVisitor;
import lombok.*;

import java.util.List;

/**
 * Created by koushikr
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AndFilter extends Filter {

  @Singular
  private List<Filter> filters;

  protected AndFilter() {
    super(FilterType.AND);
  }

  @Builder
  public AndFilter(@Singular List<Filter> filters) {
    super(FilterType.AND);
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
