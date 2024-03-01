package com.hubspot.httpql.core.filter;

public class JsonContains implements Filter {

  @Override
  public String[] names() {
    return new String[] { "json_contains" };
  }
}
