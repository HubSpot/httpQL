package com.hubspot.httpql.core.filter;

public class JsonLessThan implements Filter {

  @Override
  public String[] names() {
    return new String[] { "json_lt" };
  }
}
