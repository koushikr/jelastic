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
public class ContainsFilter extends Filter {

  private String value;

  public ContainsFilter() {
    super(FilterType.CONTAINS);
  }

  public ContainsFilter(String field, String value) {
    super(FilterType.CONTAINS, field);
    this.value = value;
  }

  @Override
  public <V> V accept(FilterVisitor<V> visitor) {
    return visitor.visit(this);
  }

}
