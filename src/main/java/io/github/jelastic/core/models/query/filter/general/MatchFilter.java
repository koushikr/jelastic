package io.github.jelastic.core.models.query.filter.general;

import io.github.jelastic.core.models.query.filter.Filter;
import io.github.jelastic.core.models.query.filter.FilterType;
import io.github.jelastic.core.models.query.filter.FilterVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MatchFilter extends Filter {

    private Object value;

    public MatchFilter() { super(FilterType.MATCH); }

    public MatchFilter(String field, Object value) {
        super(FilterType.MATCH, field);
        this.value = value;
    }

    @Override
    public <V> V accept(FilterVisitor<V> visitor) {
      return visitor.visit(this);
    }

}
