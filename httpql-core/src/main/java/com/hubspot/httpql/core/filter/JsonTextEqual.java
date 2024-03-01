package com.hubspot.httpql.core.filter;

public class JsonTextEqual implements Filter {

  @Override
  public String[] names() {
    return new String[] { "json_text_eq" };
  }
}
