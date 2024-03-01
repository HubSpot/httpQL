package com.hubspot.httpql.core.filter;

public class JsonIn implements Filter {

  @Override
  public String[] names() {
    return new String[] { "json_in" };
  }
}
