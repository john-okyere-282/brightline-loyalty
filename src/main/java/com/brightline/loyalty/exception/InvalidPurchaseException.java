package com.brightline.loyalty.exception;

public final class InvalidPurchaseException extends IllegalArgumentException {
  public InvalidPurchaseException(String message) {
    super(message);
  }
}
