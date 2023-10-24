package com.hubspot.httpql.impl;

import com.hubspot.httpql.FilterEntry;
import com.hubspot.httpql.QuerySpec;
import org.jooq.Field;

public interface JoinDescriptorImpl {

  Field<?> getField(FilterEntry filterEntry);

  JoinCondition getJoinCondition(QuerySpec querySpec);

}
