package io.github.jelastic.core.repository;

import io.github.jelastic.core.helpers.MapElement;
import io.github.jelastic.models.Query;
import io.github.jelastic.models.paged.PageWindow;
import org.elasticsearch.index.query.QueryBuilder;

import java.util.List;
import java.util.Optional;

/**
 * Created by koushikr on 13/09/17.
 * <p>
 * Place to hold all core functions related to elastic query. Initially wanted to write a
 * catalogueRepository and hold functional units, instead of a specific one.
 * <p>
 * But the specific ones will have to be written somewhere, the CRUD on ElasticSearch, didn't make a
 * lot of sense for it to be interface and implementations, as this is very specific to elastic
 * query.
 */
public interface SourceRepository {

  void closeClient();

  boolean templateExists(String templateName) throws Exception;

  Void createMapping(String indexName, String mappingType, Object mappingSource) throws Exception;

  Void createTemplate(String templateName, String indexPattern, String mappingType,
                      MapElement mappingSource, MapElement analysis) throws Exception;

  Void createIndex(String indexName) throws Exception;

  <T> Optional<T> get(String referenceId, String indexName, String mappingType, Class<T> klass)
      throws Exception;

  Void save(String indexName, String mappingType, String referenceId, String value)
      throws Exception;

  Void update(String indexName, String mappingType, String referenceId, String value)
      throws Exception;

  Void updateField(String indexName, String mappingType, String referenceId, String field,
                   Object value) throws Exception;

  Void shiftAlias(String newIndex, String aliasName) throws Exception;

  <T> List<T> search(String index, String type, Query query, Class<T> klass) throws Exception;

  <T> List<T> search(String index, String type, QueryBuilder queryBuilder, PageWindow pageWindow,
                     Class<T> klass);

  <T> List<T> searchByIds(String index, String type, List<String> ids, Class<T> klass);
}