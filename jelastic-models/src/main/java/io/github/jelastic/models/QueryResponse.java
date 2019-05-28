package io.github.jelastic.models;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by koushikr
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class QueryResponse {

  private long count;

  private List<?> entities;

}
