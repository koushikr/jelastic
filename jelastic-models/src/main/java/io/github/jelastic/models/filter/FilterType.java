package io.github.jelastic.models.filter;

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

  /* Predicates */
  String AND = "AND";
  String OR = "OR";

}
