package com.hubspot.httpql.lib.impl;

import com.hubspot.httpql.core.FilterEntry;
import com.hubspot.httpql.core.QuerySpec;
import org.jooq.Field;

public interface JoinDescriptorImpl {

  Field<?> getField(FilterEntry filterEntry);

  JoinCondition getJoinCondition(QuerySpec querySpec);

}
