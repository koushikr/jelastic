package io.github.jelastic.models.source;

import lombok.*;

/**
 * @author koushik
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GetSourceRequest<T> {

    private String referenceId;
    private String indexName;
    private String mappingType;
    private Class<T> klass;

}
