package com.hubspot.httpql.impl.filter;

import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.MultiParamConditionProvider;
import com.hubspot.httpql.core.filter.FilterIF;
import com.hubspot.httpql.core.filter.NotLike;
import java.util.Collection;
import java.util.Iterator;
import org.jooq.Condition;
import org.jooq.Field;

public class NotLikeImpl extends FilterBase implements FilterImpl {

  @Override
  public String[] names() {
    return new String[] {
        "nlike", "not_like"
    };
  }

  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new MultiParamConditionProvider<T>(field) {

      @Override
      public Condition getCondition(Collection<T> values) {
        Iterator<T> iter = values.iterator();
        Condition notLikeCondition = field.notLike((String) iter.next(), '!');
        while (iter.hasNext()) {
          notLikeCondition = notLikeCondition.and(field.notLike((String) iter.next(), '!'));
        }
        return notLikeCondition;
      }

    };
  }

  @Override
  public Class<? extends FilterIF> getAnnotationClass() {
    return NotLike.class;
  }

}
