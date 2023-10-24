package com.hubspot.httpql.lib.filter;

import com.hubspot.httpql.core.filter.Filter;
import com.hubspot.httpql.core.filter.LessThan;
import com.hubspot.httpql.lib.ConditionProvider;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Param;

public class LessThanImpl extends FilterBase implements FilterImpl {

  @Override
  public String[] names() {
    return new String[] {
        "lt"
    };
  }

  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new ConditionProvider<T>(field) {

      @Override
      public Condition getCondition(Param<T> value) {
        return field.lt(value);
      }

    };
  }

  @Override
  public Class<? extends Filter> getAnnotationClass() {
    return LessThan.class;
  }

}