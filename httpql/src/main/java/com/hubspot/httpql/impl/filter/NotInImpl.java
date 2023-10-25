package com.hubspot.httpql.impl.filter;

import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.MultiParamConditionProvider;
import com.hubspot.httpql.core.filter.FilterIF;
import com.hubspot.httpql.core.filter.NotIn;
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
  public Class<? extends FilterIF> getAnnotationClass() {
    return NotIn.class;
  }

}
