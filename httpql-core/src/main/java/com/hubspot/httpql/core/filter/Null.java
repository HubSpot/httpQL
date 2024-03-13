package com.hubspot.httpql.core.filter;

public class Null implements Filter {

  @Override
  public String[] names() {
    return new String[] { "is_null" };
  }
}
