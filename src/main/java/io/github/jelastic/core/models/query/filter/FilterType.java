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
package io.github.jelastic.core.models.query.filter;

/**
 * Created by koushikr
 */
public interface FilterType {

    /* number filters */
    String GREATER_EQUAL = "GREATER_EQUAL";
    String GREATER_THAN = "GREATER_THAN";
    String LESS_EQUAL = "LESS_EQUAL";
    String LESS_THAN = "LESS_THAN";
    String BETWEEN = "BETWEEN";

    /* general */
    String EQUALS = "EQUALS";
    String IN = "IN";
    String NOT_IN = "NOT_IN";
    String NOT_EQUALS = "NOT_EQUALS";
    String ANY = "ANY";
    String EXISTS = "EXISTS";
    String MISSING = "MISSING";
    String CONTAINS = "CONTAINS";
    String CONSTANT_SCORE = "CONSTANT_SCORE";
    String MATCH = "MATCH";

    /* Predicates */
    String AND = "AND";
    String OR = "OR";

}
