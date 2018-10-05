package com.hubspot.httpql.internal;

import java.util.Collection;

import org.jooq.Condition;

import com.hubspot.httpql.FieldFactory;
import com.hubspot.httpql.QuerySpec;

public interface FilterEntryConditionCreator<T extends QuerySpec> {

  public Condition getCondition(QuerySpec value, FieldFactory fieldFactory);

  public Collection<BoundFilterEntry<T>> getFlattenedBoundFilterEntries();
}
