package com.hubspot.httpql.core.filter;

public class IsDistinctFrom implements Filter {

  @Override
  public String[] names() {
    return new String[] { "distinct" };
  }

  @Override
  public boolean takesMultiParameters() {
    return true;
  }
}
