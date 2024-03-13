package com.hubspot.httpql.core.filter;

public class InsensitiveContains implements Filter {

  @Override
  public String[] names() {
    return new String[] { "icontains", "ilike" };
  }
}
