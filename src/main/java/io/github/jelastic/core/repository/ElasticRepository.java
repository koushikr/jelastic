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
import io.github.jelastic.core.elastic.ElasticClient;
import io.github.jelastic.core.elastic.ElasticSortBuilder;
import io.github.jelastic.core.exception.JsonMappingException;
import io.github.jelastic.core.managers.QueryManager;
import io.github.jelastic.core.models.mapping.CreateMappingRequest;
import io.github.jelastic.core.models.query.paged.PageWindow;
import io.github.jelastic.core.models.search.IdSearchRequest;
import io.github.jelastic.core.models.search.JElasticSearchRequest;
import io.github.jelastic.core.models.search.JElasticSearchResponse;
import io.github.jelastic.core.models.search.SearchRequest;
import io.github.jelastic.core.models.source.*;
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
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.cluster.metadata.IndexTemplateMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.hibernate.validator.constraints.NotEmpty;

import javax.inject.Singleton;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        if (entitySaveRequest.getRoutingKey() !=null)  indexRequestBuilder.setRouting(entitySaveRequest.getRoutingKey());

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
        if (updateEntityRequest.getRoutingKey() !=null)  updateRequestBuilder.setRouting(updateEntityRequest.getRoutingKey());
        updateRequestBuilder.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE).execute().actionGet();
    }

    public void updateField(UpdateFieldRequest updateFieldRequest) {
       validate(updateFieldRequest);

        UpdateRequest updateRequest = new UpdateRequest(
                updateFieldRequest.getIndexName(),
                updateFieldRequest.getReferenceId()
        ).retryOnConflict(updateFieldRequest.getRetryCount())
                .routing(updateFieldRequest.getRoutingKey())
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
     * @deprecated as of 7.2.0-4, replaced by {@link #enumeratedSearch(JElasticSearchRequest)}
     */
    @Deprecated
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

    /**
     * Search elasticSearch based on the search query passed.
     *
     * @param searchRequest searchRequest
     * @param <T> Response class Type
     * @throws io.github.jelastic.core.exception.InvalidQueryException when query is not built correctly.
     * @return JElasticSearchResponse<T> list of objects that meet searchCriteria
     */
    public <T> JElasticSearchResponse<T> enumeratedSearch(JElasticSearchRequest<T> searchRequest) {
        validate(searchRequest);
        val query = searchRequest.getQuery();
        QueryBuilder queryBuilder = queryManager.getQueryBuilder(query);

        SearchRequestBuilder searchRequestBuilder = elasticClient.getClient()
                .prepareSearch(searchRequest.getIndex())
                .setQuery(queryBuilder);
        if (!searchRequest.getRoutingKeys().isEmpty() && !Objects.isNull(searchRequest.getRoutingKeys())) {
            searchRequestBuilder.setRouting(
                    searchRequest.getRoutingKeys()
                            .toArray(new String[(searchRequest.getRoutingKeys().size())])
            );
        }
        if (!query.getSorters().isEmpty() && !Objects.isNull(query.getSorters())) {
            queryManager.getSortBuilders(query).forEach(searchRequestBuilder::addSort);
        }

        SearchResponse searchResponse = searchRequestBuilder
                .setFrom(query.getPageWindow().getPageNumber() * query.getPageWindow().getPageSize())
                .setSize(query.getPageWindow().getPageSize())
                .execute()
                .actionGet();

        return ElasticUtils.getSearchResponse(searchResponse, searchRequest.getKlass());
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
   * Method to delete a document based on reference id
   * @param deleteEntityRequest deleteEntityRequest
   */
  public void delete(DeleteEntityRequest deleteEntityRequest) {
        validate(deleteEntityRequest);

        DeleteRequestBuilder deleteRequestBuilder = elasticClient.getClient()
                .prepareDelete(
                deleteEntityRequest.getIndexName(),
                deleteEntityRequest.getMappingType(),
                deleteEntityRequest.getReferenceId()
                );
        deleteRequestBuilder.setRouting(deleteEntityRequest.getRoutingKey());
        deleteRequestBuilder.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                .execute().actionGet();
    }

    @Override
    public void close() {
        elasticClient.stop();
    }
}
