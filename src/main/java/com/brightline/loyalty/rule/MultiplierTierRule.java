package com.brightline.loyalty.rule;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record MultiplierTierRule(String tierName, BigDecimal multiplier) implements TierRule {
  public MultiplierTierRule {
    if (tierName == null || tierName.isBlank()) {
      throw new IllegalArgumentException("Tier name is required.");
    }
    Objects.requireNonNull(multiplier, "multiplier");
    if (multiplier.signum() <= 0) {
      throw new IllegalArgumentException("Tier multiplier must be positive.");
    }
  }

  @Override
  public int calculateTierPoints(int basePoints) {
    return multiplier.multiply(BigDecimal.valueOf(basePoints))
        .setScale(0, RoundingMode.DOWN).intValueExact();
  }
}
