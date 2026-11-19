package com.brightline.loyalty.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class BigSpenderBonusRuleTest {
  @Test
  void rejectsNullThreshold() {
    var failure = assertThrows(NullPointerException.class, () -> new BigSpenderBonusRule(null, 50));
    assertEquals("threshold", failure.getMessage());
  }

  @ParameterizedTest
  @CsvSource({
      "-1,50,Bonus threshold cannot be negative.",
      "500,0,Bonus points must be positive.",
      "500,-1,Bonus points must be positive."
  })
  void rejectsInvalidConfiguration(BigDecimal threshold, int points, String message) {
    var failure = assertThrows(IllegalArgumentException.class,
        () -> new BigSpenderBonusRule(threshold, points));
    assertEquals(message, failure.getMessage());
  }

  @Test
  void acceptsZeroThresholdAndAwardsTheConfiguredPoints() {
    var rule = new BigSpenderBonusRule(BigDecimal.ZERO, 7);
    assertEquals(7, rule.calculateBonusPoints(new BigDecimal("0.01")));
  }
}
