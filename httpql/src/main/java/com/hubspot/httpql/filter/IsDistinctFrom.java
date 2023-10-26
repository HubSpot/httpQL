package com.hubspot.httpql.filter;

import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.Filter;
import com.hubspot.httpql.MultiParamConditionProvider;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.Collection;
import java.util.stream.Collectors;

public class IsDistinctFrom extends FilterBase implements Filter {

  @Override
  public String[] names() {
    return new String[] {
        "distinct"
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
        return DSL.and(values.stream()
            .map(field::isDistinctFrom)
            .collect(Collectors.toList()));
      }
    };
  }
}
