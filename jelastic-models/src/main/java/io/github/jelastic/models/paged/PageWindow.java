package io.github.jelastic.models.paged;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;

/**
 * Created by koushikr
 */
@Data
@Builder
public class PageWindow {

  @Min(0)
  @Builder.Default
  private int pageNumber = 0;

  @Min(0)
  @Builder.Default
  private int pageSize = 10;
}
