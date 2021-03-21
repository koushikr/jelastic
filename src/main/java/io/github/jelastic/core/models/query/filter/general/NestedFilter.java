package io.github.jelastic.core.models.query.filter.general;

import com.google.common.collect.Lists;
import io.github.jelastic.core.models.query.filter.Filter;
import io.github.jelastic.core.models.query.filter.FilterType;
import io.github.jelastic.core.models.query.filter.FilterVisitor;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.lucene.search.join.ScoreMode;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NestedFilter extends Filter {

  private List<Filter> filters;

  private ScoreMode scoreMode;

  public NestedFilter(){
    super(FilterType.NESTED);
    this.filters = Lists.newArrayList();
    this.scoreMode = ScoreMode.None;
  }

  public NestedFilter(String fieldName,List<Filter> filters,ScoreMode scoreMode){
    super(FilterType.NESTED,fieldName);
    this.filters = filters;
    this.scoreMode = scoreMode;
  }

  @Override
  public <V> V accept(FilterVisitor<V> visitor) {
    return visitor.visit(this);
  }

}
