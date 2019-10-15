package com.hubspot.httpql.internal;

import org.jooq.Condition;

import com.hubspot.httpql.FieldFactory;
import com.hubspot.httpql.QuerySpec;

public class OverridableBoundFilterEntry<T extends QuerySpec> extends BoundFilterEntry<T> {

  private final Object value;

  public OverridableBoundFilterEntry(BoundFilterEntry<T> boundFilterEntry, Object value) {
    super(boundFilterEntry.getFilter(), boundFilterEntry.getProperty(), boundFilterEntry.getMeta());
    this.value = value;
  }

  @Override
  public Condition getCondition(QuerySpec value, FieldFactory fieldFactory) {
    return getConditionProvider(fieldFactory).getCondition(this.value, getProperty().getName());
  }

  public Object getValue() {
    return value;
  }
}
