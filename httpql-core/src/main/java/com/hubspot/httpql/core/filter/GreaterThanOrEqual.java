package com.hubspot.httpql.core.filter;

public class GreaterThanOrEqual implements Filter {

  @Override
  public String[] names() {
    return new String[] { "gte" };
  }
}
