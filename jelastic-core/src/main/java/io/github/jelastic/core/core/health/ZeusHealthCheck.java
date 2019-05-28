package io.github.jelastic.core.core.health;

import com.codahale.metrics.health.HealthCheck;

import javax.inject.Singleton;

/**
 * @author koushik
 */
@Singleton
public class ZeusHealthCheck extends HealthCheck{
    @Override
    protected Result check() {
        return Result.healthy();
    }
}
