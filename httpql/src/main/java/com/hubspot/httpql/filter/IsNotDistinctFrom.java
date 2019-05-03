package com.hubspot.httpql.filter;

import java.util.Collection;

import org.jooq.Condition;
import org.jooq.Field;

import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.Filter;
import com.hubspot.httpql.MultiParamConditionProvider;

public class IsNotDistinctFrom extends FilterBase implements Filter {

  @Override
  public String[] names() {
    return new String[] {
        "ndistinct"
    };
  }

  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new MultiParamConditionProvider<T>(field) {

      @Override
      public Condition getCondition(Collection<T> values) {
        return values.stream()
            .map(field::isNotDistinctFrom)
            .reduce(Condition::and)
            .orElseThrow(IllegalArgumentException::new);
      }
    };
  }
}
