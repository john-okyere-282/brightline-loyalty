package com.brightline.loyalty;

import com.brightline.loyalty.repository.InMemoryMemberRepository;
import com.brightline.loyalty.rule.LoyaltyScheme;
import com.brightline.loyalty.service.InvalidPurchaseException;
import com.brightline.loyalty.service.MemberNotFoundException;
import com.brightline.loyalty.service.PointsCalculator;
import java.math.BigDecimal;

public final class Demo {
  private Demo() {}

  public static void main(String[] args) {
    var calculator = new PointsCalculator(new InMemoryMemberRepository(), new LoyaltyScheme());
    showPurchase(calculator, "STANDARD", "m1", "100");
    showPurchase(calculator, "GOLD", "m2", "250");
    showPurchase(calculator, "SILVER", "m3", "200");
    showPurchase(calculator, "STANDARD", "m1", "500");
    showPurchase(calculator, "STANDARD", "m1", "500.01");
    showPurchase(calculator, "GOLD", "m2", "600");
    showPurchase(calculator, "Unknown member", "unknown", "100");
    showPurchase(calculator, "Invalid purchase", "m1", "-1");
  }

  private static void showPurchase(
      PointsCalculator calculator, String label, String memberId, String amount) {
    try {
      int points = calculator.calculatePoints(memberId, new BigDecimal(amount));
      System.out.println(label + ", GHS " + amount + " -> " + points);
    } catch (MemberNotFoundException | InvalidPurchaseException rejection) {
      System.err.println(label + " rejected: " + rejection.getMessage());
    }
  }
}
