package io.github.jelastic.core.utils;

import io.github.jelastic.core.repository.ElasticRepository;
import io.github.jelastic.models.source.CreateTemplateRequest;
import io.github.jelastic.models.source.GetSourceRequest;
import lombok.val;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.sort.SortOrder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author koushik
 */
public interface ElasticUtils {

    interface ElasticProperties{
        static final String NO_OF_SHARDS = "number_of_shards";
        static final String NO_OF_REPLICAS = "number_of_replicas";
        static final String INDEX_REQUEST_CACHE = "index.requests.cache.enable";
        static final String ANALYSIS = "analysis";
    }

    static SortOrder getSortOrder(io.github.jelastic.models.query.sorter.SortOrder sortOrder) {
        switch (sortOrder) {
            case ASC:
                return SortOrder.ASC;
            default:
                return SortOrder.DESC;
        }
    }

    static  <T> List<T> getResponse(SearchResponse response, Class<T> klass) {
        return Arrays.stream(response.getHits().getHits())
                .map(hit -> {
                    final Map<String, Object> result = hit.getSourceAsMap();
                    return SerDe.mapper().convertValue(result, klass);
                }).collect(Collectors.toList());
    }


    static  <T> List<T> getResponse(MultiGetResponse multiGetItemResponses, Class<T> klass) {
        return Arrays.stream(multiGetItemResponses.getResponses())
                .map(hit -> {
                    final Map<String, Object> result = hit.getResponse().getSourceAsMap();
                    return SerDe.mapper().convertValue(result, klass);
                }).collect(Collectors.toList());
    }

    static GetRequest getRequest(GetSourceRequest getSourceRequest){
        return new GetRequest(getSourceRequest.getIndexName(), getSourceRequest.getMappingType(), getSourceRequest.getReferenceId());
    }

    static Map<String, Object> getSettings(CreateTemplateRequest createTemplateRequest){
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
        return settings;
    }
}
