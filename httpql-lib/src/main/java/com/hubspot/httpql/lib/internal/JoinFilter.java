package com.hubspot.httpql.lib.internal;

import com.hubspot.httpql.core.filter.FilterIF;
import com.hubspot.httpql.lib.ConditionProvider;
import com.hubspot.httpql.lib.filter.FilterImpl;
import com.hubspot.httpql.lib.impl.JoinCondition;
import org.jooq.Field;

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
  public String[] names() {
    return filter.names();
  }

  @Override
  public <T> ConditionProvider<T> getConditionProvider(Field<T> field) {
    return filter.getConditionProvider(field);
  }

  @Override
  public Class<? extends FilterIF> getAnnotationClass() {
    return null;
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
