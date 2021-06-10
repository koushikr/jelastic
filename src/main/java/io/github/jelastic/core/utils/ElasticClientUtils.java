package io.github.jelastic.core.utils;

/*
 * Copyright 2021 Koushik R <rkoushik.14@gmail.com>.
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

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.io.Resources;
import io.github.jelastic.core.config.JElasticConfiguration;
import lombok.val;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.common.settings.Settings;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class ElasticClientUtils {

    public static CredentialsProvider getAuthCredentials(JElasticConfiguration configuration) {
        if(!configuration.getAuthConfiguration().isAuthEnabled()) return null;
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(
                        configuration.getAuthConfiguration().getUsername(),
                        configuration.getAuthConfiguration().getPassword()
                )
        );
        return credentialsProvider;
    }

    public static SSLContext getSslContext(JElasticConfiguration configuration) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, KeyManagementException {
        if(!configuration.getAuthConfiguration().isTlsEnabled()) return null;
        Path trustStorePath = Paths.get(configuration.getAuthConfiguration().getTrustStorePath());
        KeyStore truststore = KeyStore.getInstance(configuration.getAuthConfiguration().getKeyStoreType());
        try (InputStream is = Files.newInputStream(trustStorePath)) {
            truststore.load(is, configuration.getAuthConfiguration().getKeyStorePass().toCharArray());
        }
        return SSLContexts.custom()
                .loadTrustMaterial(truststore, null)
                .build();
    }

    public static String getScheme(JElasticConfiguration configuration) {
        if(null == configuration.getAuthConfiguration()) return "http";
        return configuration.getAuthConfiguration().getScheme();
    }

    public static Settings getSettings(JElasticConfiguration configuration) throws IOException {
        if(Strings.isNullOrEmpty(configuration.getSettingsFile())) return null;
        val settingsBuilder = Settings.builder();
        Path path = Paths.get(configuration.getSettingsFile());
        if (!path.toFile().exists()) {
            try {
                final URL url = Resources.getResource(configuration.getSettingsFile());
                path = new File(url.toURI()).toPath();
            } catch (URISyntaxException | NullPointerException e) {
                throw new IllegalArgumentException("settings file cannot be found", e);
            }
        }
        settingsBuilder.loadFromPath(path);
        return settingsBuilder
                .putProperties(configuration.getSettings(), (Function<String, String>) s -> s)
                .build();
    }
}
