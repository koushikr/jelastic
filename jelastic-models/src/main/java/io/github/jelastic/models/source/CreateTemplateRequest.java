package io.github.jelastic.models.source;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author koushik
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTemplateRequest {

    private String templateName;

    private String indexPattern;

    private String mappingType;

    private IndexProperties indexProperties;

    private MapElement mappingSource;

    private MapElement analysis;
}
