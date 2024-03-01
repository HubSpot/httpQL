package com.hubspot.httpql.core.filter;

public class JsonIsDistinctFrom implements Filter {

  @Override
  public String[] names() {
    return new String[] { "json_distinct" };
  }
}
