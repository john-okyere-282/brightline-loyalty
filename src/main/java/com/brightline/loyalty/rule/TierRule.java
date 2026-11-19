package com.brightline.loyalty.rule;

public interface TierRule {
  String tierName();

  /** Returns non-negative tier points for non-negative base points. */
  int calculateTierPoints(int basePoints);
}
