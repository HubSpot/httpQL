package com.hubspot.httpql.ann.desc;

import com.hubspot.httpql.QuerySpec;
import com.hubspot.httpql.impl.JoinCondition;
import com.hubspot.httpql.internal.FilterEntry;
import org.jooq.Field;

public interface JoinDescriptor {
  Field<?> getField(FilterEntry filterEntry);

  JoinCondition getJoinCondition(QuerySpec querySpec);
}
