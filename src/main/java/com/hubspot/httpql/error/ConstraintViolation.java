package com.hubspot.httpql.error;

public class ConstraintViolation extends RuntimeException {

  private static final long serialVersionUID = -6172713027225887547L;
  private final ConstraintType constraintType;
  private final int provided;
  private final int allowed;

  public ConstraintViolation(ConstraintType constraintType, LimitViolationType limitViolationType, int provided, int allowed) {
    super(String.format("%s constraint violated: %s provided, %s is %s.", constraintType, provided, limitViolationType, allowed));
    this.constraintType = constraintType;
    this.provided = provided;
    this.allowed = allowed;
  }

  public ConstraintType getConstraintType() {
    return constraintType;
  }

  public int getProvided() {
    return provided;
  }

  public int getAllowed() {
    return allowed;
  }

}
