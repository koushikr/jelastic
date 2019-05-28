package io.github.jelastic.core.elastic;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.Resources;
import com.google.common.net.HostAndPort;
import io.github.jelastic.core.config.EsConfiguration;
import io.github.jelastic.core.helpers.TransportAddressHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A Dropwizard managed Elasticsearch {@link Client}. Depending on the {@link io.github.jelastic.core.config.EsConfiguration} a
 * Node Client or a {@link TransportClient} a is being created and its lifecycle is managed by
 * Dropwizard.
 *
 * @see <a href="http://www.elasticsearch.org/guide/reference/java-api/client/#nodeclient">Node
 * Client</a>
 * @see <a href="http://www.elasticsearch.org/guide/reference/java-api/client/#transportclient">Transport
 * Client</a>
 */
@Slf4j
@Getter
public class ElasticClient {

  public final EsConfiguration esConfiguration;
  private TransportClient client;

  public ElasticClient(EsConfiguration configuration) throws IOException {
    Preconditions.checkNotNull(configuration, "Es configuration can't be null");

    this.esConfiguration = configuration;

    final Settings.Builder settingsBuilder = Settings.builder();

    if (!Strings.isNullOrEmpty(esConfiguration.getSettingsFile())) {
      Path path = Paths.get(esConfiguration.getSettingsFile());
      if (!path.toFile().exists()) {
        try {
          final URL url = Resources.getResource(esConfiguration.getSettingsFile());
          path = new File(url.toURI()).toPath();
        } catch (URISyntaxException | NullPointerException e) {
          throw new IllegalArgumentException("settings file cannot be found", e);
        }
      }
      settingsBuilder.loadFromPath(path);
    }

    final Settings settings = settingsBuilder
        .putProperties(esConfiguration.getSettings(), (Function<String, String>) s -> s)
        .put("cluster.name", esConfiguration.getClusterName())
        .build();

    this.client = new PreBuiltTransportClient(settings);

    for (HostAndPort hostAndPort : esConfiguration.getServers()) {
      this.client.addTransportAddress(TransportAddressHelper.fromHostAndPort(hostAndPort));
    }

    log.info("Started Es client");
  }


  public void stop() {
    log.info("Stopped ES client");

    if (client != null) {
      client.close();
    }

    log.info("Stopped ES client");
  }
}
