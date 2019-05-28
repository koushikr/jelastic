package io.github.jelastic.models.filter.predicate;

import java.util.List;

import io.github.jelastic.models.filter.Filter;
import io.github.jelastic.models.filter.FilterType;
import io.github.jelastic.models.filter.FilterVisitor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;

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
