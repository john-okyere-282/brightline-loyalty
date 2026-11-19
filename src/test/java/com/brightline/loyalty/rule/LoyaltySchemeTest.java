package com.brightline.loyalty.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class LoyaltySchemeTest {
  @Test
  void rejectsDuplicateTierNames() {
    var first = new MultiplierTierRule("STANDARD", BigDecimal.ONE);
    var second = new MultiplierTierRule("STANDARD", BigDecimal.TWO);

    var failure = assertThrows(IllegalArgumentException.class,
        () -> new LoyaltyScheme(List.of(first, second), List.of()));
    assertEquals("Duplicate tier registration: STANDARD", failure.getMessage());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = " ")
  void rejectsInvalidNamesFromCustomRules(String name) {
    var rule = new TierRule() {
      @Override
      public String tierName() {
        return name;
      }

      @Override
      public int calculateTierPoints(int basePoints) {
        return basePoints;
      }
    };

    var failure = assertThrows(IllegalArgumentException.class,
        () -> new LoyaltyScheme(List.of(rule), List.of()));
    assertEquals("Registered tier name is required.", failure.getMessage());
  }
}
