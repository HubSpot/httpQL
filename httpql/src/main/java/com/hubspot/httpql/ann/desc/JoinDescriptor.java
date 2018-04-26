package com.hubspot.httpql.ann.desc;

import org.jooq.Field;

import com.hubspot.httpql.QuerySpec;
import com.hubspot.httpql.impl.JoinCondition;
import com.hubspot.httpql.internal.FilterEntry;

public interface JoinDescriptor {

  Field<?> getField(FilterEntry filterEntry);

  JoinCondition getJoinCondition(QuerySpec querySpec);
}
