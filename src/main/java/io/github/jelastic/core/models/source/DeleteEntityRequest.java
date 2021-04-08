package io.github.jelastic.core.models.source;

import lombok.*;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DeleteEntityRequest {
    @NotNull
    private String indexName;
    @NotNull
    private String referenceId;
    private String routingKey;
}
