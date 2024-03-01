package com.hubspot.httpql.core.filter;

public class JsonInsensitiveContains implements Filter {

  @Override
  public String[] names() {
    return new String[] { "json_icontains", "json_ilike" };
  }
}
