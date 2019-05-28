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
public class ExistsFilter extends Filter {

  public ExistsFilter() {
    super(FilterType.EXISTS);
  }

  public ExistsFilter(String field) {
    super(FilterType.EXISTS, field);
  }

  @Override
  public <V> V accept(FilterVisitor<V> visitor) {
    return visitor.visit(this);
  }

}
