package com.brightline.loyalty.rule;

import java.math.BigDecimal;
import java.util.Objects;

public record BigSpenderBonusRule(BigDecimal threshold, int bonusPoints) implements BonusRule {
  public BigSpenderBonusRule {
    Objects.requireNonNull(threshold, "threshold");
    if (threshold.signum() < 0) {
      throw new IllegalArgumentException("Bonus threshold cannot be negative.");
    }
    if (bonusPoints <= 0) {
      throw new IllegalArgumentException("Bonus points must be positive.");
    }
  }

  @Override
  public int calculateBonusPoints(BigDecimal amount) {
    return amount.compareTo(threshold) > 0 ? bonusPoints : 0;
  }
}
