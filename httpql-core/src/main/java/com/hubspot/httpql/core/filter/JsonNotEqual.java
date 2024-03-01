package com.hubspot.httpql.core.filter;

public class JsonNotEqual implements Filter {

  @Override
  public String[] names() {
    return new String[] { "json_ne", "json_neq", "json_not" };
  }
}
