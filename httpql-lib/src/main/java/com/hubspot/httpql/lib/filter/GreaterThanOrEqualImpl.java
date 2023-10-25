package com.hubspot.httpql.lib.filter;

import com.hubspot.httpql.core.filter.FilterIF;
import com.hubspot.httpql.core.filter.GreaterThanOrEqual;
import com.hubspot.httpql.lib.ConditionProvider;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Param;

public class GreaterThanOrEqualImpl extends FilterBase implements FilterImpl {

  @Override
  public String[] names() {
    return new String[] {
        "gte"
    };
  }

  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new ConditionProvider<T>(field) {

      @Override
      public Condition getCondition(Param<T> value) {
        return field.greaterOrEqual(value);
      }

    };
  }

  @Override
  public Class<? extends FilterIF> getAnnotationClass() {
    return GreaterThanOrEqual.class;
  }

}
