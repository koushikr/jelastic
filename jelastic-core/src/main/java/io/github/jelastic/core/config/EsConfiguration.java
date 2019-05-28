package io.github.jelastic.core.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.net.HostAndPort;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by koushikr
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class EsConfiguration {

  @JsonProperty
  @NotNull
  private List<HostAndPort> servers = Collections.emptyList();

  @JsonProperty
  @NotEmpty
  private String clusterName = "elasticsearch";

  @JsonProperty
  private boolean failOnYellow = false;

  @JsonProperty
  private Map<String, String> settings = Collections.emptyMap();

  @JsonProperty
  private String settingsFile = null;

  private int numberOfShards = 1;

  private int numberOfReplicas = 1;
}
