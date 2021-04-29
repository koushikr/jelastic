/*
 * Copyright 2019 Koushik R <rkoushik.14@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.jelastic.core.elastic;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.Resources;
import io.github.jelastic.core.config.JElasticConfiguration;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Getter
public class ElasticClient {

    public final JElasticConfiguration jElasticConfiguration;
    private RestHighLevelClient client;

    public ElasticClient(JElasticConfiguration configuration) throws IOException {
        Preconditions.checkNotNull(configuration, "Es configuration can't be null");

        this.jElasticConfiguration = configuration;

        final Settings.Builder settingsBuilder = Settings.builder();

        if (!Strings.isNullOrEmpty(jElasticConfiguration.getSettingsFile())) {
            Path path = Paths.get(jElasticConfiguration.getSettingsFile());
            if (!path.toFile().exists()) {
                try {
                    final URL url = Resources.getResource(jElasticConfiguration.getSettingsFile());
                    path = new File(url.toURI()).toPath();
                } catch (URISyntaxException | NullPointerException e) {
                    throw new IllegalArgumentException("settings file cannot be found", e);
                }
            }
            settingsBuilder.loadFromPath(path);
        }

        final Settings settings = settingsBuilder
                .putProperties(jElasticConfiguration.getSettings(), (Function<String, String>) s -> s)
                .build();

        RestClientBuilder restClientBuilder = RestClient.builder(jElasticConfiguration.getServers().stream().map(hostAndPort -> new HttpHost(hostAndPort.getHost(), hostAndPort.getPort())).toArray(HttpHost[]::new));

        if(null != jElasticConfiguration.getAuthConfiguration()){
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(
                    AuthScope.ANY,
                    new UsernamePasswordCredentials(
                            jElasticConfiguration.getAuthConfiguration().getUsername(),
                            jElasticConfiguration.getAuthConfiguration().getPassword()
                    )
            );
            restClientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        }

        this.client = new RestHighLevelClient(restClientBuilder);

        if(!jElasticConfiguration.getSettings().isEmpty() || !Strings.isNullOrEmpty(jElasticConfiguration.getSettingsFile())) {
            this.client.cluster().putSettings(new ClusterUpdateSettingsRequest().transientSettings(settings), RequestOptions.DEFAULT);
        }
        log.info("Started Es client");
    }


    @SneakyThrows
    public void stop() {
        log.info("Stopped ES client");

        if (client != null) {
            client.close();
        }

        log.info("Stopped ES client");
    }
}
