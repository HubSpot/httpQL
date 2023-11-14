package com.hubspot.httpql;

public interface QuerySpec {
  /**
   * The name of the table to be queried.
   * TODO why not make this an annotation property?
   */
  String tableName();
}
