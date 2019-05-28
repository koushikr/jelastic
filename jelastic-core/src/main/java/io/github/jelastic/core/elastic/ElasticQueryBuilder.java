package io.github.jelastic.core.elastic;


import io.github.jelastic.models.filter.FilterVisitor;
import io.github.jelastic.models.filter.general.*;
import io.github.jelastic.models.filter.number.*;
import io.github.jelastic.models.filter.predicate.AndFilter;
import io.github.jelastic.models.filter.predicate.ORFilter;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import java.util.List;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Created by koushikr
 */
public class ElasticQueryBuilder implements FilterVisitor<QueryBuilder> {

  /* Fields and values are being lower-cased, before adding as clauses, since elasticsearch deals with lowercase only */
  private static Object getNormalizedValue(Object object) {
    if (object instanceof String) {
      return object;
    }
    return object;
  }

  /* Fields and values are being lower-cased, before adding as clauses, since elasticsearch deals with lowercase only */
  private static List<Object> getNormalizedValues(List<Object> object) {
    return object.stream().map(ElasticQueryBuilder::getNormalizedValue)
        .collect(Collectors.toList());
  }

  @Override
  public QueryBuilder visit(ContainsFilter filter) {
    return queryStringQuery(filter.getValue().toLowerCase())
        .defaultField(String.format("%s.analyzed", filter.getFieldName()));
  }

  @Override
  public QueryBuilder visit(LesserThanFilter filter) {
    return rangeQuery(filter.getFieldName()).lt(filter.getValue())
        .includeLower(filter.isIncludeLower()).includeUpper(filter.isIncludeUpper());
  }

  @Override
  public QueryBuilder visit(LesserEqualsFilter filter) {
    return rangeQuery(filter.getFieldName()).lte(filter.getValue())
        .includeLower(filter.isIncludeLower()).includeUpper(filter.isIncludeUpper());
  }

  @Override
  public QueryBuilder visit(GreaterThanFilter filter) {
    return rangeQuery(filter.getFieldName()).gt(filter.getValue())
        .includeLower(filter.isIncludeLower()).includeUpper(filter.isIncludeUpper());
  }

  @Override
  public QueryBuilder visit(BetweenFilter filter) {
    return rangeQuery(filter.getFieldName()).from(filter.getFrom());
  }

  @Override
  public QueryBuilder visit(GreaterEqualsFilter filter) {
    return rangeQuery(filter.getFieldName()).gte(filter.getValue())
        .includeLower(filter.isIncludeLower()).includeUpper(filter.isIncludeUpper());
  }

  @Override
  public QueryBuilder visit(NotInFilter filter) {
    return boolQuery().mustNot(termsQuery(filter.getFieldName(),
        getNormalizedValues(filter.getValues())));
  }

  @Override
  public QueryBuilder visit(NotEqualsFilter filter) {
    return boolQuery()
        .mustNot(termQuery(filter.getFieldName(), getNormalizedValue(filter.getValue())));
  }

  @Override
  public QueryBuilder visit(MissingFilter filter) {
    return boolQuery().mustNot(existsQuery(filter.getFieldName()));
  }

  @Override
  public QueryBuilder visit(InFilter filter) {
    return termsQuery(filter.getFieldName(), getNormalizedValues(filter.getValues()));
  }

  @Override
  public QueryBuilder visit(ExistsFilter filter) {
    return existsQuery(filter.getFieldName());
  }

  @Override
  public QueryBuilder visit(EqualsFilter filter) {
    return termQuery(filter.getFieldName(), getNormalizedValue(filter.getValue()));
  }

  @Override
  public QueryBuilder visit(AnyFilter filter) {
    return matchAllQuery();
  }

  @Override
  public QueryBuilder visit(AndFilter andFilter) {
    BoolQueryBuilder boolQueryBuilder = boolQuery();
    andFilter.getFilters().forEach(k -> boolQueryBuilder.must(k.accept(this)));
    return boolQueryBuilder;
  }

  @Override
  public QueryBuilder visit(ORFilter orFilter) {
    BoolQueryBuilder boolQueryBuilder = boolQuery();
    orFilter.getFilters().forEach(k -> boolQueryBuilder.should(k.accept(this)));
    return boolQueryBuilder;
  }
}
