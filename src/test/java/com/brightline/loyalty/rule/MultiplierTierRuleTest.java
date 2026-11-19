package com.brightline.loyalty.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class MultiplierTierRuleTest {
  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = " ")
  void rejectsMissingTierNames(String name) {
    var failure = assertThrows(IllegalArgumentException.class,
        () -> new MultiplierTierRule(name, BigDecimal.ONE));
    assertEquals("Tier name is required.", failure.getMessage());
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "-0.01"})
  void rejectsNonPositiveMultipliers(String multiplier) {
    var failure = assertThrows(IllegalArgumentException.class,
        () -> new MultiplierTierRule("GOLD", new BigDecimal(multiplier)));
    assertEquals("Tier multiplier must be positive.", failure.getMessage());
  }

  @Test
  void rejectsNullMultiplier() {
    var failure = assertThrows(NullPointerException.class,
        () -> new MultiplierTierRule("GOLD", null));
    assertEquals("multiplier", failure.getMessage());
  }
}
