package com.hubspot.httpql.filter;

import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.Filter;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Param;

/**
 * @deprecated Use #{@link com.hubspot.httpql.core.filter.GreaterThan}
 */
@Deprecated
public class GreaterThan extends FilterBase implements Filter {

  @Override
  public String[] names() {
    return new String[] { "gt" };
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
}
