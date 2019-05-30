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
package io.github.jelastic.core.managers;

import io.github.jelastic.core.elastic.ElasticQueryBuilder;
import io.github.jelastic.models.query.Query;
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
