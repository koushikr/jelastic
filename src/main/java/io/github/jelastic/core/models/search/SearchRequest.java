package io.github.jelastic.core.models.search;

import io.github.jelastic.core.models.query.Query;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SearchRequest<T> {
    @NotNull
    private String index;
    @NotNull
    private Query query;
    private Class<T> klass;
    private Set<String> routingKeys;
}

