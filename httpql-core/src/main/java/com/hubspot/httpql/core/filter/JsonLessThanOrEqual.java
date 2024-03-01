package com.hubspot.httpql.core.filter;

public class JsonLessThanOrEqual implements Filter {

  @Override
  public String[] names() {
    return new String[] { "json_lte" };
  }
}
