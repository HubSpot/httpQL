package com.hubspot.httpql.core.filter;

public class NotIn implements Filter {

  @Override
  public String[] names() {
    return new String[] { "nin" };
  }

  @Override
  public boolean takesMultiParameters() {
    return true;
  }
}
