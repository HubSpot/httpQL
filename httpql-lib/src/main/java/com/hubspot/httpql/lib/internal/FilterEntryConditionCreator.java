package com.hubspot.httpql.lib.internal;

import com.hubspot.httpql.lib.FieldFactory;
import com.hubspot.httpql.lib.QuerySpec;
import java.util.Collection;
import org.jooq.Condition;

public interface FilterEntryConditionCreator<T extends QuerySpec> {

  public Condition getCondition(QuerySpec value, FieldFactory fieldFactory);

  public Collection<BoundFilterEntry<T>> getFlattenedBoundFilterEntries();
}
