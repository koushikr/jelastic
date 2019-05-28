package io.github.jelastic.core.health;

import com.codahale.metrics.health.HealthCheck;
import io.github.jelastic.core.config.EsConfiguration;
import io.github.jelastic.core.elastic.ElasticClient;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.cluster.health.ClusterHealthStatus;


/**
 * Created by koushikr
 */
@Slf4j
public class EsClientHealth extends HealthCheck {

  private final ElasticClient elasticClient;
  private final EsConfiguration esConfiguration;

  public EsClientHealth(ElasticClient elasticClient, EsConfiguration esConfiguration) {
    this.elasticClient = elasticClient;
    this.esConfiguration = esConfiguration;
  }

  @Override
  protected Result check() {
    final ClusterHealthStatus status = elasticClient.getClient().admin().cluster().prepareHealth()
        .get().getStatus();

    if (status == ClusterHealthStatus.RED || (esConfiguration.isFailOnYellow()
        && status == ClusterHealthStatus.YELLOW)) {
      return Result.unhealthy("Last status: %s", status.name());
    } else {
      return Result.healthy("Last status: %s", status.name());
    }

  }
}
