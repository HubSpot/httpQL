package com.hubspot.httpql.lib.filter;

import com.hubspot.httpql.core.filter.Contains;
import com.hubspot.httpql.core.filter.FilterIF;
import com.hubspot.httpql.lib.ConditionProvider;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Param;

public class ContainsImpl extends FilterBase implements FilterImpl {

  @Override
  public String[] names() {
    return new String[] {
        "contains", "like"
    };
  }

  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new ConditionProvider<T>(field) {

      @Override
      public Condition getCondition(Param<T> value) {
        return field.contains(value);
      }

    };
  }

  @Override
  public Class<? extends FilterIF> getAnnotationClass() {
    return Contains.class;
  }

}
