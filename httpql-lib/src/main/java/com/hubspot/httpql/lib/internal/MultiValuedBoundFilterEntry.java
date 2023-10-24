package com.hubspot.httpql.lib.internal;

import com.hubspot.httpql.core.QuerySpec;
import com.hubspot.httpql.lib.FieldFactory;
import java.util.Collection;
import org.jooq.Condition;

public class MultiValuedBoundFilterEntry<T extends QuerySpec> extends BoundFilterEntry<T> {

  private final Collection<?> values;

  public MultiValuedBoundFilterEntry(BoundFilterEntry<T> boundFilterEntry, Collection<?> values) {
    super(boundFilterEntry.getFilter(), boundFilterEntry.getFilterImpl(), boundFilterEntry.getProperty(), boundFilterEntry.getMeta());
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
