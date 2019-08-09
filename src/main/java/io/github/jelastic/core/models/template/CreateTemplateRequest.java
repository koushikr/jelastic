/*
 * Copyright 2019 Koushik R <rkoushik.14@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.jelastic.core.models.template;

import io.github.jelastic.core.models.helper.MapElement;
import io.github.jelastic.core.models.index.IndexProperties;
import javax.validation.constraints.NotNull;
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
    @NotNull
    private String templateName;
    private String indexPattern;
    private String mappingType;
    private IndexProperties indexProperties;
    private MapElement mappingSource;
    private MapElement analysis;
}
