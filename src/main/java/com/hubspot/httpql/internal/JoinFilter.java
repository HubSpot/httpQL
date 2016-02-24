package com.hubspot.httpql.internal;

import org.jooq.Field;

import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.Filter;
import com.hubspot.httpql.impl.JoinCondition;

/**
 * A special type of filter which brings a join condition into the query
 */
public class JoinFilter implements Filter {

  private final Filter filter;
  private final JoinCondition join;

  public JoinFilter(Filter filter, JoinCondition join) {
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

  public Filter getFilter() {
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
