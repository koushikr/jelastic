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
public class EntitySaveRequest {

    private String indexName;
    private String mappingType;
    private String referenceId;
    private String value;

}
