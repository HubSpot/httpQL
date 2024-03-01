package com.hubspot.httpql.core.filter;

public class JsonGreaterThan implements Filter {

  @Override
  public String[] names() {
    return new String[] { "json_gt" };
  }
}
