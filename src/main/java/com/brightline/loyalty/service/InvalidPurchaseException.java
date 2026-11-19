package com.brightline.loyalty.service;

public final class InvalidPurchaseException extends IllegalArgumentException {
  public InvalidPurchaseException(String message) {
    super(message);
  }
}
