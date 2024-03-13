package com.hubspot.httpql.core.filter;

public class In implements Filter {

  @Override
  public String[] names() {
    return new String[] { "in" };
  }

  @Override
  public boolean takesMultiParameters() {
    return true;
  }
}
