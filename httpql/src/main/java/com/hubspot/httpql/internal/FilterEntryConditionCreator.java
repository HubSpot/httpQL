package com.hubspot.httpql.internal;

import com.hubspot.httpql.FieldFactory;
import com.hubspot.httpql.QuerySpec;
import java.util.Collection;
import org.jooq.Condition;

public interface FilterEntryConditionCreator<T extends QuerySpec> {
  public Condition getCondition(QuerySpec value, FieldFactory fieldFactory);

  public Collection<BoundFilterEntry<T>> getFlattenedBoundFilterEntries();
}
