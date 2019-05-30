package io.github.jelastic.models.query.filter.general;

import io.github.jelastic.models.query.filter.Filter;
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
public class NotEqualsFilter extends Filter {

  private Object value;

  public NotEqualsFilter() {
    super(FilterType.NOT_EQUALS);
  }

  public NotEqualsFilter(String field, String value) {
    super(FilterType.NOT_EQUALS, field);
    this.value = value;
  }

  @Override
  public <V> V accept(FilterVisitor<V> visitor) {
    return visitor.visit(this);
  }
}
