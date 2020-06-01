package io.github.jelastic.core.models.search;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JElasticSearchResponse<T> {

  private long count;

  private List<T> entities;
}
