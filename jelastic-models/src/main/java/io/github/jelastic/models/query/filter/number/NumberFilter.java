package io.github.jelastic.models.query.filter.number;

import io.github.jelastic.models.query.filter.Filter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by koushikr
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class NumberFilter extends Filter {

  private Number value;

  private boolean includeLower;

  private boolean includeUpper;

  protected NumberFilter(final String operator) {
    super(operator);
  }

  protected NumberFilter(final String operator, String field, Number value, boolean includeLower,
      boolean includeUpper) {
    super(operator, field);
    this.value = value;
    this.includeLower = includeLower;
    this.includeUpper = includeUpper;
  }

}
