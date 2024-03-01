package com.hubspot.httpql.filter;

import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.Filter;
import com.hubspot.httpql.MultiParamConditionProvider;
import java.util.Collection;
import org.apache.commons.lang.NotImplementedException;
import org.jooq.Condition;
import org.jooq.Field;

/**
 * @deprecated Use #{@link com.hubspot.httpql.core.filter.JsonEqual}
 */
@Deprecated
public class JsonEqual extends JsonFilterBase implements Filter {

  @Override
  public String[] names() {
    return new String[] { "json_eq" };
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new MultiParamConditionProvider<T>(field) {

      @SuppressWarnings("unchecked")
      @Override
      public Condition getCondition(Collection<T> values) {
        throw new NotImplementedException("Implemented in Impl class");
      }
    };
  }
}
