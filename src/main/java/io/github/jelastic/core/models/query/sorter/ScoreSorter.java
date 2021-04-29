package io.github.jelastic.core.models.query.sorter;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ScoreSorter extends Sorter {

    public ScoreSorter(){super(SorterType.SCORE);}

    @Builder
    public ScoreSorter(int priority, SortOrder sortOrder) {
      super(priority, sortOrder, SorterType.SCORE);
    }

    @Override
    public <V> V accept(SorterVisitor<V> visitor) {
      return visitor.visit(this);
    }
}
