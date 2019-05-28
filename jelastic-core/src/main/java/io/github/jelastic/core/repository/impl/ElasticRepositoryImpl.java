package io.github.jelastic.core.repository.impl;

import com.google.common.collect.Lists;
import io.github.jelastic.core.config.EsConfiguration;
import io.github.jelastic.core.elastic.ElasticClient;
import io.github.jelastic.core.helpers.MapElement;
import io.github.jelastic.core.managers.QueryManager;
import io.github.jelastic.core.repository.SourceRepository;
import io.github.jelastic.core.utils.SerDe;
import io.github.jelastic.models.Query;
import io.github.jelastic.models.paged.PageWindow;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesRequest;
import org.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesResponse;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilders;

import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by koushikr on 15/09/17.
 */
@Slf4j
@Singleton
public class ElasticRepositoryImpl implements SourceRepository {

  private static int updateRetries = 3;
  private final ElasticClient elasticClient;
  private final QueryManager queryManager;
  private final EsConfiguration configuration;

  public ElasticRepositoryImpl(ElasticClient client, QueryManager queryManager,
      EsConfiguration esConfiguration) {
    this.elasticClient = client;
    this.queryManager = queryManager;
    this.configuration = esConfiguration;
  }

  private org.elasticsearch.search.sort.SortOrder getSortOrder(io.github.jelastic.models.sorter.SortOrder sortOrder) {
    switch (sortOrder) {
      case ASC:
        return org.elasticsearch.search.sort.SortOrder.ASC;
      default:
        return org.elasticsearch.search.sort.SortOrder.DESC;
    }
  }

  private <T> List<T> getResponse(SearchResponse response, Class<T> klass) {
    return Arrays.stream(response.getHits().getHits())
        .map(hit -> {
          final Map<String, Object> result = hit.getSourceAsMap();
          return SerDe.mapper().convertValue(result, klass);
        }).collect(Collectors.toList());
  }

  @Override
  public Void createMapping(String indexName, String mappingType, Object mappingSource) {
    elasticClient.getClient().admin().indices().preparePutMapping(indexName).setType(mappingType)
        .setSource(mappingSource).execute().actionGet();

    return null;
  }

  @Override
  public void closeClient() {
    elasticClient.stop();
  }

  @Override
  public boolean templateExists(String templateName) {
    GetIndexTemplatesRequest getRequest = new GetIndexTemplatesRequest().names(templateName);

    final GetIndexTemplatesResponse getIndexTemplatesResponse = elasticClient.getClient().admin()
        .indices().getTemplates(getRequest).actionGet();

    return !getIndexTemplatesResponse.getIndexTemplates().isEmpty();
  }

  @Override
  public Void createTemplate(String templateName, String indexPattern, String mappingType,
                                MapElement mappingSource, MapElement analysis) {
    Map<String, Object> settings = new HashMap<>();
    settings.put("number_of_shards", configuration.getNumberOfShards());
    settings.put("number_of_replicas", configuration.getNumberOfReplicas());
    settings.put("index.requests.cache.enable", true);
    if (analysis != null) {
      settings.put("analysis", analysis);
    }

    final PutIndexTemplateRequest mapping = new PutIndexTemplateRequest()
        .name(templateName)
        .patterns(
                Lists.newArrayList("*" + indexPattern + "*")
        )
        .settings(settings)
        .mapping(mappingType, mappingSource);

    elasticClient.getClient().admin().indices().putTemplate(mapping).actionGet();

    return null;
  }

  @Override
  public Void createIndex(String indexName) throws Exception {
    if (!elasticClient.getClient().admin().indices().prepareExists(indexName).execute().get()
        .isExists()) {
      elasticClient.getClient().admin().indices().prepareCreate(indexName).execute().actionGet();
      elasticClient.getClient().admin().cluster().prepareHealth(indexName).setWaitForGreenStatus()
          .execute().actionGet();
    }

    return null;
  }

  @Override
  public <T> Optional<T> get(String referenceId, String indexName, String mappingType,
      Class<T> klass) throws Exception {
    GetResponse getResponse = elasticClient.getClient()
        .get(new GetRequest(indexName, mappingType, referenceId)).get();
    T entity =
        getResponse.isExists() ? SerDe.mapper().readValue(getResponse.getSourceAsString(), klass)
            : null;
    return Optional.ofNullable(entity);
  }

  @Override
  public Void save(String indexName, String mappingType, String referenceId, String value)
      throws Exception {
    if (!elasticClient.getClient().admin().indices().prepareExists(indexName).execute().get()
        .isExists()) {
      throw new Exception("Index, " + indexName + " doesn't exist.");
    }

    final IndexRequestBuilder indexRequestBuilder = elasticClient.getClient()
        .prepareIndex(indexName, mappingType, referenceId).setSource(value);
    indexRequestBuilder.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE).execute().get();

    return null;
  }

  @Override
  public Void update(String indexName, String mappingType, String referenceId, String value)
      throws Exception {
    UpdateRequestBuilder updateRequestBuilder = elasticClient.getClient()
        .prepareUpdate(indexName, mappingType, referenceId).setDoc(value);
    updateRequestBuilder.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE).execute().get();

    return null;
  }

  @Override
  public Void updateField(String indexName, String mappingType, String referenceId, String field,
      Object value) throws Exception {
    UpdateRequest updateRequest = new UpdateRequest(indexName, mappingType, referenceId)
        .retryOnConflict(updateRetries).doc(field, value);
    elasticClient.getClient().update(updateRequest).get();

    return null;
  }

  @Override
  public Void shiftAlias(String newIndex, String aliasName) {
    GetAliasesResponse var = elasticClient.getClient().admin().indices()
        .getAliases(new GetAliasesRequest(aliasName)).actionGet();
    ImmutableOpenMap<String, List<AliasMetaData>> aliases = var.getAliases();

    if (aliases.isEmpty()) {
      elasticClient.getClient().admin().indices().prepareAliases().addAlias(newIndex, aliasName)
          .execute().actionGet();
      return null;
    }

    String oldIndex = aliases.keysIt().next();
    if (oldIndex.equalsIgnoreCase(newIndex)) {
      return null;
    }

    elasticClient.getClient().admin().indices().prepareAliases().removeAlias(oldIndex, aliasName)
        .addAlias(newIndex, aliasName).execute().actionGet();
    elasticClient.getClient().admin().indices().delete(new DeleteIndexRequest(oldIndex))
        .actionGet();

    return null;
  }

  @Override
  public <T> List<T> search(String index, String type, Query query, Class<T> klass) throws Exception {
    QueryBuilder queryBuilder = queryManager.getQueryBuilder(query);

    SearchRequestBuilder searchRequestBuilder = elasticClient.getClient()
        .prepareSearch(index)
        .setTypes(type)
        .setQuery(queryBuilder);

    if (!query.getSorters().isEmpty()) {
      query.getSorters().forEach(sorter -> searchRequestBuilder.addSort(
          SortBuilders.fieldSort(sorter.getFieldName()).order(getSortOrder(sorter.getSortOrder()))
      ));
    }

    SearchResponse searchResponse = searchRequestBuilder
        .setFrom(query.getPageWindow().getPageNumber() * query.getPageWindow().getPageSize())
        .setSize(query.getPageWindow().getPageSize())
        .execute()
        .actionGet();

    return getResponse(searchResponse, klass);
  }


  @Override
  public <T> List<T> search(String index, String type, QueryBuilder queryBuilder,
                            PageWindow pageWindow, Class<T> klass) {

    SearchRequestBuilder searchRequestBuilder = elasticClient.getClient()
        .prepareSearch(index)
        .setTypes(type)
        .setQuery(queryBuilder);

    SearchResponse searchResponse = searchRequestBuilder
        .setFrom(pageWindow.getPageNumber() * pageWindow.getPageSize())
        .setSize(pageWindow.getPageSize())
        .execute()
        .actionGet();

    return getResponse(searchResponse, klass);
  }

  @Override
  public <T> List<T> searchByIds(String index, String type, List<String> ids, Class<T> klass) {

    MultiGetResponse multiGetItemResponses = elasticClient.getClient().prepareMultiGet().add(
        index, type, ids
    ).get();

    return getResponse(multiGetItemResponses, klass);
  }

  private <T> List<T> getResponse(MultiGetResponse multiGetItemResponses, Class<T> klass) {
    return Arrays.stream(multiGetItemResponses.getResponses())
        .map(hit -> {
          final Map<String, Object> result = hit.getResponse().getSourceAsMap();
          return SerDe.mapper().convertValue(result, klass);
        }).collect(Collectors.toList());
  }

}
