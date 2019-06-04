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
import com.google.common.net.HostAndPort;
import io.github.jelastic.core.config.JElasticConfiguration;
import io.github.jelastic.core.helpers.TransportAddressHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Getter
public class ElasticClient {

    public final JElasticConfiguration JElasticConfiguration;
    private TransportClient client;

    public ElasticClient(JElasticConfiguration configuration) throws IOException {
        Preconditions.checkNotNull(configuration, "Es configuration can't be null");

        this.JElasticConfiguration = configuration;

        final Settings.Builder settingsBuilder = Settings.builder();

        if (!Strings.isNullOrEmpty(JElasticConfiguration.getSettingsFile())) {
            Path path = Paths.get(JElasticConfiguration.getSettingsFile());
            if (!path.toFile().exists()) {
                try {
                    final URL url = Resources.getResource(JElasticConfiguration.getSettingsFile());
                    path = new File(url.toURI()).toPath();
                } catch (URISyntaxException | NullPointerException e) {
                    throw new IllegalArgumentException("settings file cannot be found", e);
                }
            }
            settingsBuilder.loadFromPath(path);
        }

        final Settings settings = settingsBuilder
                .putProperties(JElasticConfiguration.getSettings(), (Function<String, String>) s -> s)
                .put("cluster.name", JElasticConfiguration.getClusterName())
                .build();

        this.client = new PreBuiltTransportClient(settings);

        for (HostAndPort hostAndPort : JElasticConfiguration.getServers()) {
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
