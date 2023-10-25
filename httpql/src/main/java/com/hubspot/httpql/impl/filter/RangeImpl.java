package com.hubspot.httpql.impl.filter;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.MultiParamConditionProvider;
import com.hubspot.httpql.core.filter.FilterIF;
import com.hubspot.httpql.core.filter.Range;
import org.jooq.Condition;
import org.jooq.Field;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

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
  public Set<Class<? extends FilterIF>> getAnnotationClasses() {
    return ImmutableSet.of(Range.class, com.hubspot.httpql.filter.Range.class);
  }

}
