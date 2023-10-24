package com.hubspot.httpql.lib.filter;

import com.hubspot.httpql.core.filter.Filter;
import com.hubspot.httpql.core.filter.NotIn;
import com.hubspot.httpql.lib.ConditionProvider;
import com.hubspot.httpql.lib.MultiParamConditionProvider;
import java.util.Collection;
import org.jooq.Condition;
import org.jooq.Field;

public class NotInImpl extends FilterBase implements FilterImpl {

  @Override
  public String[] names() {
    return new String[] {
        "nin"
    };
  }

  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new MultiParamConditionProvider<T>(field) {

      @Override
      public Condition getCondition(Collection<T> values) {
        return field.notIn(values);
      }

    };
  }

  @Override
  public Class<? extends Filter> getAnnotationClass() {
    return NotIn.class;
  }

}