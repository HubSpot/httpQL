package com.hubspot.httpql.lib.internal;

import com.hubspot.httpql.core.HasTableName;
import com.hubspot.httpql.lib.FieldFactory;
import java.util.Collection;
import org.jooq.Condition;

public interface FilterEntryConditionCreator<T extends HasTableName> {

  public Condition getCondition(HasTableName value, FieldFactory fieldFactory);

  public Collection<BoundFilterEntry<T>> getFlattenedBoundFilterEntries();
}
