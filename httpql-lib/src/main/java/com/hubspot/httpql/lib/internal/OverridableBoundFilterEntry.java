package com.hubspot.httpql.lib.internal;

import com.hubspot.httpql.core.HasTableName;
import com.hubspot.httpql.lib.FieldFactory;
import org.jooq.Condition;

public class OverridableBoundFilterEntry<T extends HasTableName> extends BoundFilterEntry<T> {

  private final Object value;

  public OverridableBoundFilterEntry(BoundFilterEntry<T> boundFilterEntry, Object value) {
    super(boundFilterEntry.getFilter(), boundFilterEntry.getFilterImpl(), boundFilterEntry.getProperty(), boundFilterEntry.getMeta());
    this.value = value;
  }

  @Override
  public Condition getCondition(HasTableName value, FieldFactory fieldFactory) {
    return getConditionProvider(fieldFactory).getCondition(this.value, getProperty().getName());
  }

  public Object getValue() {
    return value;
  }
}
