package com.hubspot.httpql.internal;

import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.core.filter.Filter;
import com.hubspot.httpql.impl.JoinCondition;
import com.hubspot.httpql.impl.filter.FilterImpl;
import org.jooq.Field;

import java.util.Collections;
import java.util.Set;

/**
 * A special type of filter which brings a join condition into the query
 */
public class JoinFilter implements FilterImpl {

  private final FilterImpl filter;
  private final JoinCondition join;

  public JoinFilter(FilterImpl filter, JoinCondition join) {
    this.filter = filter;
    this.join = join;
  }

  @Override
  public Set<Class<? extends Filter>> getAnnotationClasses() {
    return Collections.emptySet();
  }

  @Override
  public <T> ConditionProvider<T> getConditionProvider(Field<T> field) {
    return filter.getConditionProvider(field);
  }

  public FilterImpl getFilter() {
    return filter;
  }

  public JoinCondition getJoin() {
    return join;
  }

  @Override
  public boolean equals(Object obj) {
    return filter.equals(obj);
  }

  @Override
  public int hashCode() {
    return filter.hashCode();
  }

}
