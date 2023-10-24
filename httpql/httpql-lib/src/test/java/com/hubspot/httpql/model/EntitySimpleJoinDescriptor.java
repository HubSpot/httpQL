package com.hubspot.httpql.model;

import com.hubspot.httpql.FilterEntry;
import com.hubspot.httpql.QuerySpec;
import com.hubspot.httpql.impl.JoinCondition;
import com.hubspot.httpql.impl.JoinDescriptorImpl;

import org.jooq.Field;
import org.jooq.impl.DSL;

public class EntitySimpleJoinDescriptor implements JoinDescriptorImpl {
  private static final String JOIN_TABLE_NAME = "join_tbl";

  @Override
  public Field<?> getField(FilterEntry filterEntry) {
    return DSL.field(DSL.name(JOIN_TABLE_NAME, filterEntry.getQueryName()));
  }

  @Override
  public JoinCondition getJoinCondition(QuerySpec querySpec) {
    return new JoinCondition(DSL.table(DSL.name(JOIN_TABLE_NAME)),
        DSL.field(DSL.name(querySpec.tableName(), "id"))
            .eq(DSL.field(DSL.name(JOIN_TABLE_NAME, "entity_id"))));
  }
}
