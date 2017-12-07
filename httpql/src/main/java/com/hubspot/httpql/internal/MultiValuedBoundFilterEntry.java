package com.hubspot.httpql.internal;

import java.util.Collection;

import org.jooq.Condition;

import com.hubspot.httpql.FieldFactory;
import com.hubspot.httpql.QuerySpec;

public class MultiValuedBoundFilterEntry<T extends QuerySpec> extends BoundFilterEntry<T> {

  private final Collection<?> values;

  public MultiValuedBoundFilterEntry(BoundFilterEntry<T> boundFilterEntry, Collection<?> values) {
    super(boundFilterEntry.getFilter(), boundFilterEntry.getProperty(), boundFilterEntry.getMeta());
    this.values = values;
  }

  @Override
  public Condition getCondition(QuerySpec value, FieldFactory fieldFactory) {
    return getConditionProvider(fieldFactory).getCondition(values, getProperty().getName());
  }

  public Collection<?> getValues() {
    return values;
  }

}
