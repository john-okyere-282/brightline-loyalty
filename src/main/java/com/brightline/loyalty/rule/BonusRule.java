package com.brightline.loyalty.rule;

import java.math.BigDecimal;

@FunctionalInterface
public interface BonusRule {
  /** Returns non-negative bonus points for a valid purchase amount. */
  int calculateBonusPoints(BigDecimal amount);
}
