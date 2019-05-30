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
public class LesserEqualsFilter extends NumberFilter {

  public LesserEqualsFilter() {
    super(FilterType.LESS_EQUAL);
  }

  public LesserEqualsFilter(String field, Number value, boolean includeLower,
      boolean includeUpper) {
    super(FilterType.LESS_EQUAL, field, value, includeLower, includeUpper);
  }

  @Override
  public <V> V accept(FilterVisitor<V> visitor) {
    return visitor.visit(this);
  }
}
