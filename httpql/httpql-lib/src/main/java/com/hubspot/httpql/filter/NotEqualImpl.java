package com.hubspot.httpql.filter;

import com.hubspot.httpql.ConditionProvider;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Param;

public class NotEqualImpl extends FilterBase implements FilterImpl {

  @Override
  public String[] names() {
    return new String[] {
        "ne", "neq", "not"
    };
  }

  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new ConditionProvider<T>(field) {

      @Override
      public Condition getCondition(Param<T> value) {
        return field.isNull().or(field.notEqual(value));
      }

    };
  }

  @Override
  public Class<? extends Filter> getAnnotationClass() {
    return NotEqual.class;
  }

}
