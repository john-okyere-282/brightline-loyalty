package com.brightline.loyalty.exception;

public final class MemberNotFoundException extends RuntimeException {
  public MemberNotFoundException() {
    super("Member was not found.");
  }
}
