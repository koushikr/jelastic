package io.github.jelastic.models.source;

import lombok.*;

import java.util.List;

/**
 * @author koushik
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class IdSearchRequest<T> {

    private String index;
    private String type;
    private List<String> ids;
    private Class<T> klass;
}
