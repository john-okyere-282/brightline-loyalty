package com.brightline.loyalty.service;

public final class UnknownTierException extends RuntimeException {
  public UnknownTierException() {
    super("Member has no registered tier rule.");
  }
}
