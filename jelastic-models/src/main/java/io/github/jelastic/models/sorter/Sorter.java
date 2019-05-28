package io.github.jelastic.models.sorter;

import com.google.common.collect.ComparisonChain;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;

/**
 * Created by koushikr
 */
@Data
@Builder
public class Sorter implements Comparable<Sorter> {

  @Min(1)
  public int priority;

  @NonNull
  @NotEmpty
  private String fieldName;

  private SortOrder sortOrder;

  @Override
  public int compareTo(Sorter o) {
    return ComparisonChain.start()
        .compare(priority, o.getPriority())
        .result();
  }
}
