package com.brightline.loyalty;

import com.brightline.loyalty.exception.InvalidPurchaseException;
import com.brightline.loyalty.exception.MemberNotFoundException;
import com.brightline.loyalty.repository.InMemoryMemberRepository;
import com.brightline.loyalty.rule.LoyaltyScheme;
import com.brightline.loyalty.service.PointsCalculator;
import java.math.BigDecimal;

public final class Demo {
  private final PointsCalculator calculator =
      new PointsCalculator(new InMemoryMemberRepository(), new LoyaltyScheme());

  private Demo() {}

  public static void main(String[] args) {
    new Demo().run();
  }

  private void run() {
    showPurchase("STANDARD", "m1", "100");
    showPurchase("GOLD", "m2", "250");
    showPurchase("SILVER", "m3", "200");
    showPurchase("STANDARD", "m1", "500");
    showPurchase("STANDARD", "m1", "500.01");
    showPurchase("GOLD", "m2", "600");
    showPurchase("Unknown member", "unknown", "100");
    showPurchase("Invalid purchase", "m1", "-1");
  }

  private void showPurchase(String label, String memberId, String amount) {
    try {
      int points = calculator.calculatePoints(memberId, new BigDecimal(amount));
      System.out.println(label + ", GHS " + amount + " -> " + points);
    } catch (MemberNotFoundException | InvalidPurchaseException rejection) {
      System.err.println(label + " rejected: " + rejection.getMessage());
    }
  }
}
