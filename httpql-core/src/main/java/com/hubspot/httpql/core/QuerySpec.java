package com.hubspot.httpql.core;


import com.hubspot.httpql.core.ann.FilterBy;
import com.hubspot.httpql.core.ann.OrderBy;

/**
 * Core interface for classes providing filter information via, e.g., {@link FilterBy} and {@link OrderBy}.
 *
 * @author tdavis
 */
public interface QuerySpec {
  /**
   * The name of the table to be queried.
   * TODO why not make this an annotation property?
   */
  String tableName();
}
