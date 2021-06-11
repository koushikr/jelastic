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

import com.google.common.base.Preconditions;
import io.github.jelastic.core.config.JElasticConfiguration;
import io.github.jelastic.core.utils.ElasticClientUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import javax.inject.Singleton;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@Slf4j
@Getter
@Singleton
public class ElasticClient {

    public final JElasticConfiguration jElasticConfiguration;
    private RestHighLevelClient client;

    public ElasticClient(JElasticConfiguration configuration) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, KeyManagementException {
        Preconditions.checkNotNull(configuration, "Es configuration can't be null");
        this.jElasticConfiguration = configuration;
        this.initializeClient();
        log.info("Started Es client");
    }

    private void initializeClient() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
        RestClientBuilder restClientBuilder = RestClient.builder(
                jElasticConfiguration.getServers().stream().map(hostAndPort -> new HttpHost(hostAndPort.getHost(), hostAndPort.getPort(),
                        ElasticClientUtils.getScheme(jElasticConfiguration))).toArray(HttpHost[]::new)
        );
        if(null != jElasticConfiguration.getAuthConfiguration()){
            val sslContext = ElasticClientUtils.getSslContext(jElasticConfiguration);
            val credentialsProvider = ElasticClientUtils.getAuthCredentials(jElasticConfiguration);
            restClientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder -> {
                if(null != credentialsProvider) {
                    httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
                if(null != sslContext){
                    httpAsyncClientBuilder.setSSLContext(sslContext);
                }
                return httpAsyncClientBuilder;
            });
        }
        this.client = new RestHighLevelClient(restClientBuilder);
        val settings = ElasticClientUtils.getSettings(jElasticConfiguration);
        if(null != settings){
            this.client.cluster().putSettings(new ClusterUpdateSettingsRequest().transientSettings(settings), RequestOptions.DEFAULT);
        }
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
