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
public class UpdateFieldRequest {

    private String indexName;
    private String mappingType;
    private String referenceId;
    private String field;
    private Object value;
    private int retryCount;

}
