package com.brightline.loyalty.service;

public final class MemberNotFoundException extends RuntimeException {
  public MemberNotFoundException() {
    super("Member was not found.");
  }
}
