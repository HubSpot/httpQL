package com.hubspot.httpql.filter;

import com.hubspot.httpql.ConditionProvider;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Param;

public class LessThanOrEqualImpl extends FilterBase implements FilterImpl {

  @Override
  public String[] names() {
    return new String[] {
        "lte"
    };
  }

  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new ConditionProvider<T>(field) {

      @Override
      public Condition getCondition(Param<T> value) {
        return field.lessOrEqual(value);
      }

    };
  }

  @Override
  public Class<? extends Filter> getAnnotationClass() {
    return LessThanOrEqual.class;
  }

}
