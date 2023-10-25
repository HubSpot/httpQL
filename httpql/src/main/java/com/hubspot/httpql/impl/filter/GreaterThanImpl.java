package com.hubspot.httpql.impl.filter;

import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.core.filter.FilterIF;
import com.hubspot.httpql.core.filter.GreaterThan;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Param;

public class GreaterThanImpl extends FilterBase implements FilterImpl {

  @Override
  public String[] names() {
    return new String[] {
        "gt"
    };
  }

  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new ConditionProvider<T>(field) {

      @Override
      public Condition getCondition(Param<T> value) {
        return field.gt(value);
      }

    };
  }

  @Override
  public Class<? extends FilterIF> getAnnotationClass() {
    return GreaterThan.class;
  }

}
