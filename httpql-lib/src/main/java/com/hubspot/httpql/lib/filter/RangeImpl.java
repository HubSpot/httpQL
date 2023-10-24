package com.hubspot.httpql.lib.filter;

import com.google.common.base.Preconditions;
import com.hubspot.httpql.core.filter.Filter;
import com.hubspot.httpql.core.filter.Range;
import com.hubspot.httpql.lib.ConditionProvider;
import com.hubspot.httpql.lib.MultiParamConditionProvider;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.jooq.Condition;
import org.jooq.Field;

public class RangeImpl extends FilterBase implements FilterImpl {

  private static final Comparator<Number> NUMBER_COMPARATOR = new Comparator<Number>() {
    @Override
    public int compare(Number a, Number b) {
      return new BigDecimal(a.toString()).compareTo(new BigDecimal(b.toString()));
    }
  };

  @Override
  public String[] names() {
    return new String[] {
        "range"
    };
  }

  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new MultiParamConditionProvider<T>(field) {

      @SuppressWarnings("unchecked")
      @Override
      public Condition getCondition(Collection<T> values) {
        List<Number> valueList = new ArrayList<Number>((Collection<? extends Number>) values);
        Preconditions.checkArgument(valueList.size() == 2, "Range filters require exactly 2 parameters");

        Collections.sort(valueList, NUMBER_COMPARATOR);
        return field.between((T) valueList.get(0), (T) valueList.get(1));
      }

    };
  }

  @Override
  public Class<? extends Filter> getAnnotationClass() {
    return Range.class;
  }

}
