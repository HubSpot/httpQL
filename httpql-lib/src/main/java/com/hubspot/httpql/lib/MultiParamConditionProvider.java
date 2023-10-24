package com.hubspot.httpql.lib;

import java.util.Collection;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.conf.ParamType;

/**
 * Like {@link ConditionProvider} for filters that require multi-value parameters.
 * <p>
 * Filters utilizing these types of {@link Condition} are not currently compatible with {@link ParamType.NAMED}.
 *
 * @author tdavis
 */
public abstract class MultiParamConditionProvider<T> extends ConditionProvider<T> {

  public MultiParamConditionProvider(Field<T> field) {
    super(field);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Condition getCondition(Object obj, String paramName) {
    return getCondition((Collection<T>) obj, paramName);
  }

  public Condition getCondition(Collection<T> values, String paramName) {
    return getCondition(values);
  }

  @Override
  public Condition getCondition(Param<T> value) {
    throw new RuntimeException("Single-value getCondition() called for multi-value filter!");
  }

  public abstract Condition getCondition(Collection<T> values);

}
