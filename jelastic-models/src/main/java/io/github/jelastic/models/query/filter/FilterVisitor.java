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
