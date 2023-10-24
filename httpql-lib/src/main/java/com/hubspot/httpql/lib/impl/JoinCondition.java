package com.hubspot.httpql.lib.impl;

import org.jooq.Condition;
import org.jooq.TableLike;

public class JoinCondition {
  private final TableLike<?> table;
  private final Condition condition;
  private final boolean leftJoin;

  public JoinCondition(TableLike<?> table, Condition condition, boolean leftJoin) {
    this.table = table;
    this.condition = condition;
    this.leftJoin = leftJoin;
  }

  public JoinCondition(TableLike<?> table, Condition condition) {
    this(table, condition, false);
  }

  public TableLike<?> getTable() {
    return table;
  }

  public Condition getCondition() {
    return condition;
  }

  public boolean isLeftJoin() {
    return leftJoin;
  }

}
