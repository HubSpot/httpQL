package com.hubspot.httpql.core.filter;

public class JsonIsNotDistinctFrom implements Filter {

  @Override
  public String[] names() {
    return new String[] { "json_ndistinct" };
  }
}
