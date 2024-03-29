package com.hubspot.httpql.model;

import com.hubspot.httpql.QuerySpec;
import com.hubspot.httpql.ann.desc.JoinDescriptor;
import com.hubspot.httpql.impl.JoinCondition;
import com.hubspot.httpql.internal.FilterEntry;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

public class EntityComplexJoinDescriptor implements JoinDescriptor {

  private static final String JOIN_TABLE_NAME = "join_tbl";

  @Override
  public Field<?> getField(FilterEntry filterEntry) {
    return DSL.nvl(DSL.field(DSL.name(JOIN_TABLE_NAME, filterEntry.getQueryName())), 0);
  }

  @Override
  public JoinCondition getJoinCondition(QuerySpec querySpec) {
    Condition joinCondition1 = DSL
      .field(DSL.name(querySpec.tableName(), "group_id"))
      .eq(DSL.field(DSL.name(JOIN_TABLE_NAME, "id")));
    Condition joinCondition2 = DSL
      .field(DSL.name(querySpec.tableName(), "tag"))
      .eq(DSL.field(DSL.name(JOIN_TABLE_NAME, "id")));
    Condition joinCondition3 = DSL
      .field(DSL.name(JOIN_TABLE_NAME, "meta_type"))
      .eq("joinObjects");

    return new JoinCondition(
      DSL.table(DSL.name(JOIN_TABLE_NAME)),
      joinCondition1.and(joinCondition2).and(joinCondition3),
      true
    );
  }
}
