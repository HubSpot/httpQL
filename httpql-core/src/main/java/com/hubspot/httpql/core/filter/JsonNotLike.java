package com.hubspot.httpql.core.filter;

public class JsonNotLike implements Filter {

  @Override
  public String[] names() {
    return new String[] { "json_nlike", "json_not_like" };
  }
}
