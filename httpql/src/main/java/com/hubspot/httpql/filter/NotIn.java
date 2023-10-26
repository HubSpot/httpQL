package com.hubspot.httpql.filter;

import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.Filter;
import com.hubspot.httpql.MultiParamConditionProvider;
import org.jooq.Condition;
import org.jooq.Field;

import java.util.Collection;

public class NotIn extends FilterBase implements Filter {

  @Override
  public String[] names() {
    return new String[] {
        "nin"
    };
  }

  @Override
  public boolean takesMultiParameters() {
    return true;
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

}
