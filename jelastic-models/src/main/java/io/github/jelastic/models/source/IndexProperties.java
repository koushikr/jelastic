package io.github.jelastic.models.source;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author koushik
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IndexProperties {

    private int noOfShards;

    private int noOfReplicas;

    private boolean enableRequestCache;
}
