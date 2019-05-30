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
