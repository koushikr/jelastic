package io.github.jelastic.core.models.search;

import io.github.jelastic.core.models.query.JElasticQuery;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class JElasticSearchRequest<T> {
    @NotNull
    private String index;
    @NotNull
    private JElasticQuery query;
    private Class<T> klass;
    private Set<String> routingKeys;
}

