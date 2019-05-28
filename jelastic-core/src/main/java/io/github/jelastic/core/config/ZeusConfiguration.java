package io.github.jelastic.core.config;

import com.google.common.base.Preconditions;
import io.github.jelastic.core.utils.ZeusUtils;
import com.phonepe.zeus.models.Destination;
import com.phonepe.zeus.models.namespace.NamespaceProfile;
import com.phonepe.zeus.models.namespace.NamespaceType;
import com.phonepe.zeus.models.strategy.AAPinning;
import com.phonepe.zeus.models.strategy.APPinning;
import com.phonepe.zeus.models.strategy.PinningStrategy;
import com.phonepe.zeus.models.strategy.PinningStrategyVisitor;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author koushik
 *
 * This is the configuratin we'd keep in rosey and will initialize the pinning bundle with.
 *
 * The computation of what to give away to app comes from this app!
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ZeusConfiguration {

    @NotNull
    private PinningStrategy strategy;

    @NotNull @NotEmpty
    private List<NamespaceProfile> namespaces;

    private Map<String, Destination> whitelist;

    public boolean whitelisted(String pinningKey){
        return !ZeusUtils.isNullOrEmpty(getWhitelist()) &&
                getWhitelist().containsKey(pinningKey);
    }

    public void validate(){
        Preconditions.checkArgument(!getNamespaces().isEmpty(), "No namespace configuration found");

        getStrategy().accept(new PinningStrategyVisitor<Void>() {
            @Override
            public Void visit(AAPinning aaMeta) {
                Arrays.asList(NamespaceType.values()).forEach(
                        namespaceType -> Arrays.asList(Destination.values()).forEach(
                                destination -> Preconditions.checkArgument(ZeusUtils.valid(
                        getNamespaces(), destination, namespaceType
                ), String.format("No configuration found for destination %s and namespace %s",
                        destination,
                        namespaceType))));
                return null;
            }

            @Override
            public Void visit(APPinning apMeta) {
                Arrays.asList(NamespaceType.values()).forEach(namespaceType -> Preconditions.checkArgument(ZeusUtils.valid(
                        getNamespaces(), apMeta.getPrimary(), namespaceType
                ), String.format("No configuration found for destination %s and namespace %s",
                        apMeta.getPrimary(),
                        namespaceType)));
                return null;
            }
        });
    }
}
