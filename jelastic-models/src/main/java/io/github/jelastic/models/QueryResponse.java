package io.github.jelastic.models;

import lombok.*;

import java.util.List;

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
