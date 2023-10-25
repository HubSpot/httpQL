package com.hubspot.httpql;

import com.hubspot.httpql.core.filter.FilterIF;
import org.jooq.Field;

/**
 * Effectively the operator in a WHERE clause condition.
 *
 * @author tdavis
 */
public interface Filter extends FilterIF {
  /**
   * List of names the operator goes by in queries; the {@code gt} in {@code foo_gt=1}
   */
  String[] names();

  <T> ConditionProvider<T> getConditionProvider(Field<T> field);

}
