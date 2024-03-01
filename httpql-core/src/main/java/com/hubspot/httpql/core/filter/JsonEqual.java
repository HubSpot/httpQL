package com.hubspot.httpql.core.filter;

public class JsonEqual implements Filter {

  @Override
  public String[] names() {
    return new String[] { "json_eq" };
  }
}
