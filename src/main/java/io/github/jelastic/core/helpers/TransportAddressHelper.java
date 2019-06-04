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
package io.github.jelastic.core.helpers;

import com.google.common.net.HostAndPort;
import org.elasticsearch.common.transport.TransportAddress;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Created by koushikr
 */
public class TransportAddressHelper {

    private static final int DEFAULT_PORT = 9300;

    /**
     * Convert a {@link HostAndPort} instance to {@link TransportAddress}. If the {@link HostAndPort}
     * instance doesn't contain a port the resulting {@link TransportAddress} will have {@link
     * #DEFAULT_PORT} as port.
     *
     * @param hostAndPort a valid {@link HostAndPort} instance
     * @return a {@link TransportAddress} equivalent to the provided {@link HostAndPort} instance
     */
    public static TransportAddress fromHostAndPort(final HostAndPort hostAndPort) {
        InetSocketAddress address = new InetSocketAddress(hostAndPort.getHost(),
                hostAndPort.getPortOrDefault(DEFAULT_PORT));
        return new TransportAddress(address);
    }

    /**
     * Convert a list of {@link HostAndPort} instances to an array of {@link TransportAddress}
     * instances.
     *
     * @param hostAndPorts a {@link List} of valid {@link HostAndPort} instances
     * @return an array of {@link TransportAddress} instances
     * @see #fromHostAndPort(com.google.common.net.HostAndPort)
     */
    public static TransportAddress[] fromHostAndPorts(final List<HostAndPort> hostAndPorts) {
        if (hostAndPorts == null) {
            return new TransportAddress[0];
        } else {
            TransportAddress[] transportAddresses = new TransportAddress[hostAndPorts.size()];

            for (int i = 0; i < hostAndPorts.size(); i++) {
                transportAddresses[i] = fromHostAndPort(hostAndPorts.get(i));
            }

            return transportAddresses;
        }
    }
}
