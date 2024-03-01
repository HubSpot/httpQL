package com.hubspot.httpql.core.filter;

public class JsonNull implements Filter {

  @Override
  public String[] names() {
    return new String[] { "json_is_null" };
  }
}
