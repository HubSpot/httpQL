package com.hubspot.httpql.error;

public class UnknownFieldException extends RuntimeException {
  private static final long serialVersionUID = -5969282691775554298L;

  public UnknownFieldException(String message) {
    super(message);
  }
}
