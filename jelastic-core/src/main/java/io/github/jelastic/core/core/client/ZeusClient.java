package io.github.jelastic.core.core.client;

import com.google.common.base.Strings;
import io.github.jelastic.core.config.ZeusConfiguration;
import io.github.jelastic.core.core.exception.ZeusException;
import io.github.jelastic.core.utils.ZeusUtils;
import com.phonepe.zeus.models.Destination;
import com.phonepe.zeus.models.PinningResponse;
import com.phonepe.zeus.models.namespace.NamespaceProfile;
import com.phonepe.zeus.models.strategy.AAPinning;
import com.phonepe.zeus.models.strategy.APPinning;
import com.phonepe.zeus.models.strategy.PinningStrategyVisitor;
import io.appform.functionmetrics.MonitoredFunction;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author koushik
 */
@Slf4j
@Singleton
@Getter
public class ZeusClient {

    @NotNull
    private final ZeusConfiguration configuration;

    public ZeusClient(ZeusConfiguration configuration){
        this.configuration = configuration;
    }

    private Destination getDestination(String pinningKey){
        if(configuration.whitelisted(pinningKey)){
            return configuration.getWhitelist().get(pinningKey);
        }

        return configuration.getStrategy().accept(
                new PinningStrategyVisitor<Destination>() {
                    @Override
                    public Destination visit(AAPinning aaMeta) {
                        Optional<Destination> optionalDestination = ZeusUtils.getDestination(
                                pinningKey);
                        if(!optionalDestination.isPresent()){
                            throw ZeusException.error(ZeusException.ErrorCode.BAD_REQUEST);
                        }
                        return optionalDestination.get();
                    }

                    @Override
                    public Destination visit(APPinning apMeta) {
                        return apMeta.getPrimary();
                    }
                });
    }


    @MonitoredFunction(className = "ZeusClient", method = "getPinningResponse")
    public PinningResponse getPinningResponse(String pinningKey){
        if(Strings.isNullOrEmpty(pinningKey)){
            throw ZeusException.error(ZeusException.ErrorCode.BAD_REQUEST);
        }

        val destination = getDestination(pinningKey);

        //Don't need a second guard rail here! since during onboarding a config we check for its validity
        List<NamespaceProfile> namespaceProfiles = configuration.getNamespaces()
                .stream()
                .filter(namespace -> namespace.getDestination() == destination)
                .collect(Collectors.toList());

        return PinningResponse.builder()
                .namespaces(namespaceProfiles)
                .build();
    }
}
