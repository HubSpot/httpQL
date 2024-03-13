package com.hubspot.httpql;

import org.jooq.Field;

/**
 * Effectively the operator in a WHERE clause condition.
 *
 * @author tdavis
 */
public interface Filter extends com.hubspot.httpql.core.filter.Filter {
  /**
   * List of names the operator goes by in queries; the {@code gt} in {@code foo_gt=1}
   */

  <T> ConditionProvider<T> getConditionProvider(Field<T> field);
}
