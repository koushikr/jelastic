package io.github.jelastic.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.jelastic.core.config.ZeusConfiguration;
import io.github.jelastic.core.core.client.ZeusClient;
import io.github.jelastic.core.core.exception.ZeusExceptionMapper;
import io.github.jelastic.core.core.health.ZeusHealthCheck;
import io.github.jelastic.core.resources.ZeusResource;
import io.appform.functionmetrics.FunctionMetricsManager;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author koushik
 */
@NoArgsConstructor
public abstract class ZeusBundle<T extends Configuration> implements ConfiguredBundle<T> {

    public abstract ZeusConfiguration getPinningConfiguration(T configuration);

    @Getter
    private ZeusClient zeusClient;


    /**
     * Sets the objectMapper properties and initializes ZeusClient, along with its health check
     * @param configuration     {@link T}               The typed config against which the said TrouperBundle is initialized
     * @param environment       {@link Environment}     The dropwizard environment object.
     */
    @Override
    public void run(T configuration, Environment environment){
        environment.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        environment.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        environment.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        environment.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        environment.getObjectMapper().configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        environment.getObjectMapper().configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);

        ZeusConfiguration zeusConfiguration = getPinningConfiguration(configuration);
        zeusConfiguration.validate();

        zeusClient = new ZeusClient(zeusConfiguration);

        environment.healthChecks().register("zeus-bundle-health-check", new ZeusHealthCheck());
        environment.jersey().register(new ZeusExceptionMapper());
        environment.jersey().register(new ZeusResource(zeusClient));

        FunctionMetricsManager.initialize("commands", environment.metrics());
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }


}
