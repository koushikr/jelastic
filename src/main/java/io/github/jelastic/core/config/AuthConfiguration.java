package io.github.jelastic.core.config;

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

import com.google.common.base.Strings;
import io.dropwizard.validation.ValidationMethod;
import lombok.*;



@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Data
public class AuthConfiguration {

    private boolean authEnabled;

    private String username;

    private String password;

    private boolean tlsEnabled;

    private String trustStorePath;

    private String keyStoreType;

    private String keyStorePass;

    @ValidationMethod(message = "One of Auth/TLS Configuration is not Valid")
    public boolean isValidInput(){
        if (isAuthEnabled() && invalidAuthConfiguration()) {
            return false;
        }

        if (isTlsEnabled() && invalidTlsConfiguration()) {
            return false;
        }

        return true;
    }

    private boolean invalidAuthConfiguration() {
        return Strings.isNullOrEmpty(username) || Strings.isNullOrEmpty(password);
    }

    private boolean invalidTlsConfiguration() {
        return Strings.isNullOrEmpty(trustStorePath) || Strings.isNullOrEmpty(keyStoreType)
            || Strings.isNullOrEmpty(keyStorePass);
    }
}
