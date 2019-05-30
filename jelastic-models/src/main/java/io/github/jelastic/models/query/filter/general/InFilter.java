package io.github.jelastic.models.query.filter.general;

import io.github.jelastic.models.query.filter.Filter;
import io.github.jelastic.models.query.filter.FilterType;
import io.github.jelastic.models.query.filter.FilterVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * Created by koushikr
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InFilter extends Filter {

  private List<Object> values;

  public InFilter() {
    super(FilterType.IN);
  }

  public InFilter(String field, List<Object> values) {
    super(FilterType.IN, field);
    this.values = values;
  }

  @Override
  public <V> V accept(FilterVisitor<V> visitor) {
    return visitor.visit(this);
  }
}
