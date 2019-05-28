package io.github.jelastic.core.managers;

import io.github.jelastic.core.elastic.ElasticQueryBuilder;
import io.github.jelastic.models.Query;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import javax.inject.Singleton;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;

/**
 * Created by koushikr on 19/09/17.
 */
@Slf4j
@Singleton
public class QueryManager {

  private ElasticQueryBuilder elasticQueryBuilder;

  public QueryManager() {
    this.elasticQueryBuilder = new ElasticQueryBuilder();
  }

  public QueryBuilder getQueryBuilder(Query query) throws Exception {
    BoolQueryBuilder boolQueryBuilder = boolQuery();

    try {
      query.getFilters().forEach(k -> boolQueryBuilder.must(k.accept(elasticQueryBuilder)));
    } catch (Exception e) {
        throw new Exception("Query incorrect: " + e.getMessage(), e);
    }

    return boolQueryBuilder;
  }

}
