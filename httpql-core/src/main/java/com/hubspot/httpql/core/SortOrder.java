package com.hubspot.httpql.core;

import java.util.Arrays;

public enum SortOrder {
  /**
   * Ascending sort order.
   */
  ASC("asc"),

  /**
   * Descending sort order.
   */
  DESC("desc"),

  /**
   * Default sort order.
   */
  DEFAULT("");

  private final String sql;

  SortOrder(String sql) {
    this.sql = sql;
  }

  public final String toSQL() {
    return sql;
  }

  public static SortOrder fromOrderString(String orderString) {
    return Arrays
      .stream(values())
      .filter(o -> o.toSQL().equals(orderString.toLowerCase()))
      .findAny()
      .orElse(DEFAULT);
  }
}
