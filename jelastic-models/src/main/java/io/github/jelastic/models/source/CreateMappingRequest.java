package io.github.jelastic.models.source;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * @author koushik
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateMappingRequest {

    @NotNull
    @NotEmpty
    private String indexName;

    @NotNull
    @NotEmpty
    private String mappingType;

    @NotNull
    private Object mappingSource;

}
