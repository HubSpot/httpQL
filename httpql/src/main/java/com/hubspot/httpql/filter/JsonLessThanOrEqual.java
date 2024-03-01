package com.hubspot.httpql.filter;

import com.hubspot.httpql.Filter;
import com.hubspot.httpql.MultiParamConditionProvider;
import java.util.Collection;
import org.apache.commons.lang.NotImplementedException;
import org.jooq.Condition;
import org.jooq.Field;

/**
 * @deprecated Use #{@link com.hubspot.httpql.core.filter.JsonLessThanOrEqual}
 */
@Deprecated
public class JsonLessThanOrEqual extends JsonFilterBase implements Filter {

  @Override
  public String[] names() {
    return new String[] { "json_lte" };
  }

  @Override
  public <T> MultiParamConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new MultiParamConditionProvider<T>(field) {

      @Override
      public Condition getCondition(Collection<T> values) {
        throw new NotImplementedException("Implemented in Impl class");
      }
    };
  }
}
