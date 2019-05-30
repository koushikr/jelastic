package io.github.jelastic.models.query.filter;

import io.github.jelastic.models.query.filter.general.*;
import io.github.jelastic.models.query.filter.number.*;
import io.github.jelastic.models.query.filter.predicate.AndFilter;
import io.github.jelastic.models.query.filter.predicate.ORFilter;

/**
 * Created by koushikr
 */
public interface FilterVisitor<T> {

  T visit(ContainsFilter filter);

  T visit(LesserThanFilter filter);

  T visit(LesserEqualsFilter filter);

  T visit(GreaterThanFilter filter);

  T visit(BetweenFilter filter);

  T visit(GreaterEqualsFilter filter);

  T visit(NotInFilter filter);

  T visit(NotEqualsFilter filter);

  T visit(MissingFilter filter);

  T visit(InFilter filter);

  default T visit(ExistsFilter filter) {
    return null;
  }

  T visit(EqualsFilter filter);

  T visit(AnyFilter filter);

  T visit(AndFilter andFilter);

  T visit(ORFilter orFilter);

}
