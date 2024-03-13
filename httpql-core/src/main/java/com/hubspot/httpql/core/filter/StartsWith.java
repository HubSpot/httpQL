package com.hubspot.httpql.core.filter;

public class StartsWith implements Filter {

  @Override
  public String[] names() {
    return new String[] { "startswith" };
  }
}
