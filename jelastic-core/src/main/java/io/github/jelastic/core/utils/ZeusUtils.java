package io.github.jelastic.core.utils;

import com.google.common.hash.Hashing;
import com.phonepe.zeus.models.Destination;
import com.phonepe.zeus.models.namespace.NamespaceProfile;
import com.phonepe.zeus.models.namespace.NamespaceType;
import io.appform.functionmetrics.MonitoredFunction;
import lombok.val;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author koushik
 */
public interface ZeusUtils {

    static boolean isNullOrEmpty(Map map) {
        return null == map
                || map.isEmpty();
    }

    @MonitoredFunction(className = "ZeusUtils", method = "getDestination")
    static Optional<Destination> getDestination(String pinningKey){
        val tag  = Math.abs(Hashing.murmur3_128()
                .newHasher()
                .putBytes(pinningKey.getBytes()).hash().asInt() % Destination.values().length);
        return Arrays.stream(Destination.values())
                .filter(destination -> destination.getId() == tag)
                .findFirst();

    }

    static boolean valid(List<NamespaceProfile> namespaces,
                                Destination destination,
                                NamespaceType namespaceType){
        return namespaces.stream()
                .anyMatch(each -> each.getDestination() == destination && each.getNamespace() == namespaceType);
    }

}
