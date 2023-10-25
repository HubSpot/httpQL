package com.hubspot.httpql.impl.filter;

import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.core.filter.FilterIF;
import org.jooq.Field;

import java.util.Set;

/**
 * Effectively the operator in a WHERE clause condition.
 *
 * @author tdavis
 */
public interface FilterImpl {
  /**
   * List of names the operator goes by in queries; the {@code gt} in {@code foo_gt=1}
   */
  String[] names();

  <T> ConditionProvider<T> getConditionProvider(Field<T> field);

  Set<Class<? extends FilterIF>> getAnnotationClasses();

}
