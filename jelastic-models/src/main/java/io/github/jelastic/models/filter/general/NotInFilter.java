package io.github.jelastic.models.filter.general;

import io.github.jelastic.models.filter.Filter;
import io.github.jelastic.models.filter.FilterType;
import io.github.jelastic.models.filter.FilterVisitor;
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
public class NotInFilter extends Filter {

  private List<Object> values;

  public NotInFilter() {
    super(FilterType.NOT_IN);
  }

  public NotInFilter(String field, List<Object> values) {
    super(FilterType.NOT_IN, field);
    this.values = values;
  }

  @Override
  public <V> V accept(FilterVisitor<V> visitor) {
    return visitor.visit(this);
  }


}
