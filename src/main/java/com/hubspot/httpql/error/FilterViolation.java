package com.hubspot.httpql.error;

public class FilterViolation extends RuntimeException {
  private static final long serialVersionUID = 8443663063595464570L;

  public FilterViolation(String message) {
    super(message);
  }
}
