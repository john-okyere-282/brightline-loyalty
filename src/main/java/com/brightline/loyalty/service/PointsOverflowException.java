package com.brightline.loyalty.service;

public final class PointsOverflowException extends RuntimeException {
  public PointsOverflowException(ArithmeticException cause) {
    super("Calculated points exceed the supported integer range.", cause);
  }
}
