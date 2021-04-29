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
package io.github.jelastic.core.utils;

import io.github.jelastic.core.models.search.SearchResponse;
import io.github.jelastic.core.models.source.GetSourceRequest;
import io.github.jelastic.core.models.template.CreateTemplateRequest;
import lombok.val;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.search.sort.SortOrder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author koushik
 */
public interface ElasticUtils {

    static SortOrder getSortOrder(io.github.jelastic.core.models.query.sorter.SortOrder sortOrder) {
        switch (sortOrder) {
            case ASC:
                return SortOrder.ASC;
            default:
                return SortOrder.DESC;
        }
    }

    static <T> List<T> getResponse(org.elasticsearch.action.search.SearchResponse response, Class<T> klass) {
        return Arrays.stream(response.getHits().getHits())
                .map(hit -> {
                    final Map<String, Object> result = hit.getSourceAsMap();
                    return MapperUtils.mapper().convertValue(result, klass);
                }).collect(Collectors.toList());
    }

    static <T> SearchResponse<T> getSearchResponse(org.elasticsearch.action.search.SearchResponse response, Class<T> klass) {
        return SearchResponse.<T>builder()
                .count(response.getHits().getTotalHits().value)
                .entities(Arrays.stream(response.getHits().getHits())
                        .map(hit -> {
                            final Map<String, Object> result = hit.getSourceAsMap();
                            return MapperUtils.mapper().convertValue(result, klass);
                        }).collect(Collectors.toList()))
                .build();
    }

    static <T> List<T> getResponse(MultiGetResponse multiGetItemResponses, Class<T> klass) {
        return Arrays.stream(multiGetItemResponses.getResponses())
                .map(hit -> {
                    final Map<String, Object> result = hit.getResponse().getSourceAsMap();
                    return MapperUtils.mapper().convertValue(result, klass);
                }).collect(Collectors.toList());
    }

    static GetRequest getRequest(GetSourceRequest getSourceRequest) {
        return new GetRequest(getSourceRequest.getIndexName()).id(getSourceRequest.getReferenceId());
    }

    static Map<String, Object> getSettings(CreateTemplateRequest createTemplateRequest) {
        val indexProperties = createTemplateRequest.getIndexProperties();
        Map<String, Object> settings = new HashMap<String, Object>() {
            {
                put(ElasticProperties.NO_OF_SHARDS, indexProperties.getNoOfShards());
                put(ElasticProperties.NO_OF_REPLICAS, indexProperties.getNoOfReplicas());
                put(ElasticProperties.INDEX_REQUEST_CACHE, indexProperties.isEnableRequestCache());
            }
        };
        if (!Objects.isNull(createTemplateRequest.getAnalysis())) {
            settings.put(ElasticProperties.ANALYSIS, createTemplateRequest.getAnalysis());
        }
        if (!Objects.isNull(createTemplateRequest.getIndexProperties().getNoOfRoutingShards())) {
            settings.put(ElasticProperties.NO_OF_ROUTING_SHARDS,
                    createTemplateRequest.getIndexProperties().getNoOfRoutingShards());
        }
        return settings;
    }

    interface ElasticProperties {
        String NO_OF_SHARDS = "number_of_shards";
        String NO_OF_REPLICAS = "number_of_replicas";
        String INDEX_REQUEST_CACHE = "index.requests.cache.enable";
        String ANALYSIS = "analysis";
        String NO_OF_ROUTING_SHARDS = "number_of_routing_shards";
    }
}
