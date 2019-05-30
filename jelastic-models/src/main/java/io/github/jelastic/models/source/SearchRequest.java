package io.github.jelastic.models.source;

import io.github.jelastic.models.query.Query;
import lombok.*;

/**
 * @author koushik
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SearchRequest<T> {
    private String index;
    private String type;
    private Query query;
    private Class<T> klass;
}
