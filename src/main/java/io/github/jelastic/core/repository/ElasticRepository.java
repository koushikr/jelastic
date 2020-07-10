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
package io.github.jelastic.core.repository;

import com.google.common.collect.Lists;
import io.github.jelastic.core.config.JElasticConfiguration;
import io.github.jelastic.core.elastic.ElasticClient;
import io.github.jelastic.core.exception.JelasticException;
import io.github.jelastic.core.exception.JsonMappingException;
import io.github.jelastic.core.managers.QueryManager;
import io.github.jelastic.core.models.mapping.CreateMappingRequest;
import io.github.jelastic.core.models.query.Query;
import io.github.jelastic.core.models.query.paged.PageWindow;
import io.github.jelastic.core.models.search.IdSearchRequest;
import io.github.jelastic.core.models.search.SearchRequest;
import io.github.jelastic.core.models.source.EntitySaveRequest;
import io.github.jelastic.core.models.source.GetSourceRequest;
import io.github.jelastic.core.models.source.UpdateEntityRequest;
import io.github.jelastic.core.models.source.UpdateFieldRequest;
import io.github.jelastic.core.models.template.CreateTemplateRequest;
import io.github.jelastic.core.utils.ElasticUtils;
import io.github.jelastic.core.utils.MapperUtils;
import io.github.jelastic.core.utils.ValidationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesRequest;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.cluster.metadata.IndexTemplateMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.sort.SortBuilders;
import org.hibernate.validator.constraints.NotEmpty;

import javax.inject.Singleton;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by koushikr
 */
@Slf4j
@Singleton
@AllArgsConstructor
@Getter
public class ElasticRepository implements Closeable {

    private final ElasticClient elasticClient;
    private final QueryManager queryManager;
    private final JElasticConfiguration JElasticConfiguration;

    public IndexTemplateMetaData getTemplate(@NotEmpty String templateName) {
        GetIndexTemplatesRequest getRequest = new GetIndexTemplatesRequest().names(templateName);
        val getIndexTemplatesResponse = elasticClient.getClient().admin()
                .indices().getTemplates(getRequest).actionGet();
        return getIndexTemplatesResponse.getIndexTemplates().isEmpty() ?
                null : getIndexTemplatesResponse.getIndexTemplates().get(0);
    }

    /**
     * Runs with the {@link ValidationUtil} when runWithValidator is true.
     * Works with the request validation of jaxrs!
     * @param request Any java object
     */
    public <T> void validate(T request){
        if(elasticClient.getJElasticConfiguration().isRunWithValidator()){
            ValidationUtil.validateRequest(request);
        }
    }

    public void createMapping(CreateMappingRequest mappingRequest) {
        validate(mappingRequest);

        elasticClient.getClient()
                .admin()
                .indices()
                .preparePutMapping(mappingRequest.getIndexName())
                .setType(mappingRequest.getMappingType())
                .setSource(mappingRequest.getMappingSource())
                .execute()
                .actionGet();
    }

    public void createTemplate(CreateTemplateRequest createTemplateRequest) {
        validate(createTemplateRequest);

        val mapping = new PutIndexTemplateRequest()
                .name(createTemplateRequest.getTemplateName())
                .patterns(
                        Lists.newArrayList(
                                "*" + createTemplateRequest.getIndexPattern() + "*"
                        )
                )
                .settings(ElasticUtils.getSettings(createTemplateRequest))
                .mapping(
                        createTemplateRequest.getMappingType(),
                        createTemplateRequest.getMappingSource()
                );
        elasticClient.getClient().admin().indices().putTemplate(mapping).actionGet();
    }

    public void createIndex(@NotEmpty String indexName) {
        if (!elasticClient.getClient().admin().indices().prepareExists(indexName).execute().actionGet()
                .isExists()) {
            elasticClient.getClient()
                    .admin()
                    .indices()
                    .prepareCreate(indexName)
                    .execute()
                    .actionGet();

            elasticClient.getClient()
                    .admin()
                    .cluster()
                    .prepareHealth(indexName)
                    .setWaitForGreenStatus()
                    .execute()
                    .actionGet();
        }
    }

    /**
     * Method to retrieve document based on referenceId.
     *
     * @param getSourceRequest getSourceRequest
     * @param <T> class to be retrieved
     * @throws JsonMappingException when there is IOException, JsonParseException & JsonMappingException
     * @return Optional<T>
     */
    public <T> Optional<T> get(GetSourceRequest<T> getSourceRequest) {
        validate(getSourceRequest);

        GetResponse getResponse = elasticClient.getClient()
                .get(ElasticUtils.getRequest(getSourceRequest)).actionGet();
        try{
            return Optional.ofNullable(
                    getResponse.isExists() ?
                    MapperUtils.mapper().readValue(
                        getResponse.getSourceAsString(), getSourceRequest.getKlass()
                    ) : null
            );
        } catch (IOException io) {
            throw new JsonMappingException("Exception while mapping response to class ", io);
        }
    }

    /**
     * Method to persist entity into ElasticSearch.
     *
     * @param entitySaveRequest entitySaveRequest
     * @throws IndexNotFoundException when index is not created.
     */
    public void save(EntitySaveRequest entitySaveRequest) {
        validate(entitySaveRequest);

        if (!elasticClient.getClient().admin()
                .indices()
                .prepareExists(entitySaveRequest.getIndexName())
                .execute()
                .actionGet()
                .isExists()
                ) {
            throw new IndexNotFoundException("Index, " + entitySaveRequest.getIndexName() + " doesn't exist.");
        }
        val indexRequestBuilder = elasticClient.getClient()
                .prepareIndex(
                        entitySaveRequest.getIndexName(),
                        entitySaveRequest.getMappingType(),
                        entitySaveRequest.getReferenceId()
                )
                .setSource(entitySaveRequest.getValue(), XContentType.JSON);
        indexRequestBuilder.setRefreshPolicy(
                WriteRequest.RefreshPolicy.IMMEDIATE
        ).execute().actionGet();
    }

    public void update(UpdateEntityRequest updateEntityRequest) {
        validate(updateEntityRequest);

        UpdateRequestBuilder updateRequestBuilder = elasticClient.getClient()
                .prepareUpdate(
                        updateEntityRequest.getIndexName(),
                        updateEntityRequest.getMappingType(),
                        updateEntityRequest.getReferenceId()
                )
                .setDoc(updateEntityRequest.getValue(), XContentType.JSON);
        updateRequestBuilder.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE).execute().actionGet();
    }

    public void updateField(UpdateFieldRequest updateFieldRequest) {
       validate(updateFieldRequest);

        UpdateRequest updateRequest = new UpdateRequest(
                updateFieldRequest.getIndexName(),
                updateFieldRequest.getReferenceId()
        ).retryOnConflict(updateFieldRequest.getRetryCount())
            .doc(
                MapperUtils.writeValueAsString(updateFieldRequest.getFieldValueMap()),
                XContentType.JSON
            );
        elasticClient.getClient().update(updateRequest).actionGet();
    }

    public void reAlias(@NotEmpty  String newIndex, @NotEmpty String aliasName) {
        GetAliasesResponse var = elasticClient.getClient().admin().indices()
                .getAliases(new GetAliasesRequest(aliasName)).actionGet();
        ImmutableOpenMap<String, List<AliasMetaData>> aliases = var.getAliases();

        if (aliases.isEmpty()) {
            elasticClient.getClient()
                    .admin()
                    .indices()
                    .prepareAliases()
                    .addAlias(newIndex, aliasName)
                    .execute()
                    .actionGet();
        }

        String oldIndex = aliases.keysIt().next();
        if (oldIndex.equalsIgnoreCase(newIndex)) return;

        elasticClient.getClient()
                .admin()
                .indices()
                .prepareAliases()
                .removeAlias(oldIndex, aliasName)
                .addAlias(newIndex, aliasName)
                .execute()
                .actionGet();
        elasticClient.getClient()
                .admin()
                .indices()
                .delete(new DeleteIndexRequest(oldIndex))
                .actionGet();
    }

    /**
     * Search elasticSearch based on the search query passed.
     *
     * @param searchRequest searchRequest
     * @param <T> Response class Type
     * @throws io.github.jelastic.core.exception.InvalidQueryException when query is not built correctly.
     * @return List<T> list of objects that meet searchCriteria
     */
    public <T> List<T> search(SearchRequest<T> searchRequest) {
        validate(searchRequest);
        val query = searchRequest.getQuery();
        QueryBuilder queryBuilder = queryManager.getQueryBuilder(query);

        SearchRequestBuilder searchRequestBuilder = elasticClient.getClient()
                .prepareSearch(searchRequest.getIndex())
                .setQuery(queryBuilder);

        if (!query.getSorters().isEmpty()) {
            query.getSorters().forEach(sorter -> searchRequestBuilder.addSort(
                    SortBuilders.fieldSort(sorter.getFieldName())
                            .order(ElasticUtils.getSortOrder(sorter.getSortOrder()))
            ));
        }

        SearchResponse searchResponse = searchRequestBuilder
                .setFrom(query.getPageWindow().getPageNumber() * query.getPageWindow().getPageSize())
                .setSize(query.getPageWindow().getPageSize())
                .execute()
                .actionGet();

        return ElasticUtils.getResponse(searchResponse, searchRequest.getKlass());
    }

    public <T> List<T> search(String index, QueryBuilder queryBuilder,
                                    PageWindow pageWindow, Class<T> klass) {

        SearchRequestBuilder searchRequestBuilder = elasticClient.getClient()
                .prepareSearch(index)
                .setQuery(queryBuilder);

        SearchResponse searchResponse = searchRequestBuilder
                .setFrom(pageWindow.getPageNumber() * pageWindow.getPageSize())
                .setSize(pageWindow.getPageSize())
                .execute()
                .actionGet();

        return ElasticUtils.getResponse(searchResponse, klass);
    }

    public <T> List<T> searchByIds(IdSearchRequest<T> idSearchRequest) {
        validate(idSearchRequest);

        MultiGetResponse multiGetItemResponses = elasticClient.getClient().prepareMultiGet().add(
                idSearchRequest.getIndex(),
                idSearchRequest.getType(),
                idSearchRequest.getIds()
        ).get();

        return ElasticUtils.getResponse(multiGetItemResponses, idSearchRequest.getKlass());
    }

    /**
     * Load data of entire index based on batchSize using ES scroll API
     * During first request to ES we send scroll ttl and the response return result along with scroll Id. Then for all
     * subsequent we send this scroll id as request param. ES knows how much data is returned in previous calls. We fetch
     * the data in batches using batchSize which should be < 10k for better performance
     *
     * @param <T> Response class Type
     * @param index Index to be loaded
     * @param query jelastic query object
     * @param batchSize Will tell ElasticClient the size of each fetch, should be <= 10000 for better performance
     * @param fetchSize The desired result size
     * @return List<T> list of all objects in that index
     */
    public <T> List<T> loadAll(String index, Query query, int batchSize, int fetchSize, Class<T> klass) {
        final int maxResultSize = JElasticConfiguration.getMaxResultSize();

        if(fetchSize > maxResultSize){
            log.error("Result size exceeds configured limit of {}, Please try changing it.", maxResultSize);
            throw new JelasticException("Result size exceeds configured limit.");
        }

        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
        QueryBuilder queryBuilder = queryManager.getQueryBuilder(query);
        SearchRequestBuilder searchRequestBuilder = elasticClient
                .getClient()
                .prepareSearch(index)
                .setQuery(queryBuilder)
                .setSize(batchSize)
                .setScroll(scroll);
        SearchResponse searchResponse = searchRequestBuilder
                .execute()
                .actionGet();

        String scrollId = searchResponse.getScrollId();
        List<T> batchedResult = ElasticUtils.getResponse(searchResponse, klass);
        List<T> totalResult = new ArrayList<>(batchedResult);
        int count = 1;
        while (!batchedResult.isEmpty()) {
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            scrollRequest.scroll(scroll);
            searchResponse = elasticClient
                    .getClient()
                    .searchScroll(scrollRequest)
                    .actionGet();
            batchedResult = ElasticUtils.getResponse(searchResponse, klass);
            totalResult.addAll(batchedResult);

        }
        return totalResult;
    }

    @Override
    public void close() {
        elasticClient.stop();
    }
}
