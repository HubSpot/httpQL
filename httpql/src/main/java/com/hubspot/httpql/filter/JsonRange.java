package com.hubspot.httpql.filter;

import com.hubspot.httpql.Filter;
import com.hubspot.httpql.MultiParamConditionProvider;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import org.apache.commons.lang.NotImplementedException;
import org.jooq.Condition;
import org.jooq.Field;

/**
 * @deprecated Use #{@link com.hubspot.httpql.core.filter.JsonRange}
 */
@Deprecated
public class JsonRange extends JsonFilterBase implements Filter {

  @Override
  public String[] names() {
    return new String[] { "json_range" };
  }

  @Override
  public <T> MultiParamConditionProvider<T> getConditionProvider(final Field<T> field) {
    final Comparator<T> NUMBER_COMPARATOR = Comparator.comparing(
      a -> new BigDecimal(a.toString())
    );

    return new MultiParamConditionProvider<T>(field) {

      @Override
      public Condition getCondition(Collection<T> values) {
        throw new NotImplementedException("Implemented in Impl class");
      }
    };
  }
}
