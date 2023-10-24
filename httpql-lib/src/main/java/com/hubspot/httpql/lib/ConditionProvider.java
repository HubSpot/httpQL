package com.hubspot.httpql.lib;

import com.hubspot.httpql.core.filter.Filter;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.impl.DSL;

/**
 * Used by {@link Filter} implementations to create appropriately-typed {@link Condition} instances.
 * <p>
 * Because Filters are (necessarily) un-typed and {@link Field} and {@link Param} instances must share the same type parameter, this class acts as a closure of sorts to make sure that's possible.
 *
 * @author tdavis
 */
public abstract class ConditionProvider<T> {

  private final Field<T> field;

  public ConditionProvider(Field<T> field) {
    this.field = field;
  }

  public Field<T> getField() {
    return field;
  }

  public Param<T> getParam(Object value, String paramName) {
    Param<T> param = DSL.param(paramName, field.getType());
    param.setConverted(value);
    return param;
  }

  public Condition getCondition(Object value, String paramName) {
    return getCondition(getParam(value, paramName));
  }

  public abstract Condition getCondition(Param<T> value);

}
