package com.hubspot.httpql.core.filter;

public class JsonNotNull implements Filter {

  @Override
  public String[] names() {
    return new String[] { "json_not_null" };
  }
}
