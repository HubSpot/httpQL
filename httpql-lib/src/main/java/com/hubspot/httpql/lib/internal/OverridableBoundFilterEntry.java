package com.hubspot.httpql.lib.internal;

import com.hubspot.httpql.lib.FieldFactory;
import com.hubspot.httpql.lib.QuerySpec;
import org.jooq.Condition;

public class OverridableBoundFilterEntry<T extends QuerySpec> extends BoundFilterEntry<T> {

  private final Object value;

  public OverridableBoundFilterEntry(BoundFilterEntry<T> boundFilterEntry, Object value) {
    super(boundFilterEntry.getFilter(), boundFilterEntry.getFilterImpl(), boundFilterEntry.getProperty(), boundFilterEntry.getMeta());
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
