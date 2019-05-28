package io.github.jelastic.models.filter.number;

import io.github.jelastic.models.filter.Filter;
import io.github.jelastic.models.filter.FilterType;
import io.github.jelastic.models.filter.FilterVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by koushikr
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BetweenFilter extends Filter {

  private Number from;

  private Number to;

  public BetweenFilter() {
    super(FilterType.BETWEEN);
  }

  public BetweenFilter(String field, Number from, Number to) {
    super(FilterType.BETWEEN, field);
    this.from = from;
    this.to = to;
  }

  @Override
  public <V> V accept(FilterVisitor<V> visitor) {
    return visitor.visit(this);
  }
}
