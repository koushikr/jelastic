package io.github.jelastic.models.query.filter.number;

import io.github.jelastic.models.query.filter.FilterType;
import io.github.jelastic.models.query.filter.FilterVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by koushikr
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GreaterThanFilter extends NumberFilter {

  public GreaterThanFilter() {
    super(FilterType.GREATER_THAN);
  }

  public GreaterThanFilter(String field, Number value, boolean includeLower, boolean includeUpper) {
    super(FilterType.GREATER_THAN, field, value, includeLower, includeUpper);
  }

  @Override
  public <V> V accept(FilterVisitor<V> visitor) {
    return visitor.visit(this);
  }

}
