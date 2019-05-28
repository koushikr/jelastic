package io.github.jelastic.models.filter.general;

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
public class EqualsFilter extends Filter {

  private Object value;

  public EqualsFilter() {
    super(FilterType.EQUALS);
  }

  public EqualsFilter(String operator) {
    super(operator);
  }

  public EqualsFilter(String operator, String field) {
    super(operator, field);
  }

  public EqualsFilter(String field, Object value) {
    super(FilterType.EQUALS, field);
    this.value = value;
  }

  public EqualsFilter(String operator, String field, Object value) {
    super(operator, field);
    this.value = value;
  }

  @Override
  public <V> V accept(FilterVisitor<V> visitor) {
    return visitor.visit(this);
  }

}
