package com.brightline.loyalty.exception;

public final class UnknownTierException extends RuntimeException {
  public UnknownTierException() {
    super("Member has no registered tier rule.");
  }
}
