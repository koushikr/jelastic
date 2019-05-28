package io.github.jelastic.models.filter.number;

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
public class LesserThanFilter extends NumberFilter {

  public LesserThanFilter() {
    super(FilterType.LESS_THAN);
  }

  public LesserThanFilter(String field, Number value, boolean includeLower, boolean includeUpper) {
    super(FilterType.LESS_THAN, field, value, includeLower, includeUpper);
  }

  @Override
  public <V> V accept(FilterVisitor<V> visitor) {
    return visitor.visit(this);
  }
}
