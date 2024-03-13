package com.hubspot.httpql.filter;

import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.Filter;
import com.hubspot.httpql.MultiParamConditionProvider;
import java.util.Collection;
import java.util.Iterator;
import org.jooq.Condition;
import org.jooq.Field;

/**
 * @deprecated Use #{@link com.hubspot.httpql.core.filter.NotLike}
 */
@Deprecated
public class NotLike extends FilterBase implements Filter {

  @Override
  public String[] names() {
    return new String[] { "nlike", "not_like" };
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
        Iterator<T> iter = values.iterator();
        Condition notLikeCondition = field.notLike((String) iter.next(), '!');
        while (iter.hasNext()) {
          notLikeCondition =
            notLikeCondition.and(field.notLike((String) iter.next(), '!'));
        }
        return notLikeCondition;
      }
    };
  }
}
