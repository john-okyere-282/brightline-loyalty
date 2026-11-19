package com.brightline.loyalty.rule;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class LoyaltyScheme {
  private static final BigDecimal STANDARD_MULTIPLIER = BigDecimal.ONE;
  private static final BigDecimal SILVER_MULTIPLIER = new BigDecimal("1.5");
  private static final BigDecimal GOLD_MULTIPLIER = BigDecimal.valueOf(2);

  private static final BigDecimal BIG_SPENDER_THRESHOLD = BigDecimal.valueOf(500);
  private static final int BIG_SPENDER_BONUS = 50;

  private final Map<String, TierRule> tiers;
  private final List<BonusRule> bonuses;

  public LoyaltyScheme() {
    this(List.of(
        new MultiplierTierRule("STANDARD", STANDARD_MULTIPLIER),
        new MultiplierTierRule("SILVER", SILVER_MULTIPLIER),
        new MultiplierTierRule("GOLD", GOLD_MULTIPLIER)),
        List.of(new BigSpenderBonusRule(BIG_SPENDER_THRESHOLD, BIG_SPENDER_BONUS)));
  }

  public LoyaltyScheme(List<TierRule> tierRules, List<BonusRule> bonusRules) {
    bonuses = List.copyOf(bonusRules);
    Map<String, TierRule> registered = new HashMap<>();
    for (TierRule rule : tierRules) {
      String tierName = rule.tierName();
      if (tierName == null || tierName.isBlank()) {
        throw new IllegalArgumentException("Registered tier name is required.");
      }
      if (registered.putIfAbsent(tierName, rule) != null) {
        throw new IllegalArgumentException("Duplicate tier registration: " + tierName);
      }
    }
    tiers = Collections.unmodifiableMap(registered);
  }

  public Optional<TierRule> findTier(String tierName) {
    return Optional.ofNullable(tiers.get(tierName));
  }

  public List<BonusRule> bonusRules() {
    return bonuses;
  }
}
