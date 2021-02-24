package io.github.jelastic.core.models.query.filter.general;

import io.github.jelastic.core.models.query.filter.Filter;
import io.github.jelastic.core.models.query.filter.FilterType;
import io.github.jelastic.core.models.query.filter.FilterVisitor;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ConstantScoreFilter extends Filter {

    @Singular
    private List<Filter> filters;

    protected ConstantScoreFilter() {
      super(FilterType.CONSTANT_SCORE);
    }

    @Builder
    public ConstantScoreFilter(@Singular List<Filter> filters) {
        super(FilterType.CONSTANT_SCORE);
        this.filters = filters;
    }

    @Override
    public boolean validate() {
      return filters.stream().map(Filter::validate).reduce((x, y) -> x && y).orElse(false);
    }

    @Override
    public <V> V accept(FilterVisitor<V> visitor) {
    return visitor.visit(this);
  }
}

