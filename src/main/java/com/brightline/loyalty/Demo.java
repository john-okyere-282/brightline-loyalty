package com.brightline.loyalty;

import com.brightline.loyalty.repository.InMemoryMemberRepository;
import com.brightline.loyalty.service.MemberNotFoundException;
import com.brightline.loyalty.service.PointsCalculator;
import java.math.BigDecimal;

public final class Demo {
  private Demo() {}

  public static void main(String[] args) {
    PointsCalculator calculator = new PointsCalculator(new InMemoryMemberRepository());
    System.out.println("STANDARD, GHS 100 -> "
        + calculator.calculatePoints("m1", BigDecimal.valueOf(100)));
    System.out.println("GOLD, GHS 250 -> "
        + calculator.calculatePoints("m2", BigDecimal.valueOf(250)));
    try {
      calculator.calculatePoints("unknown", BigDecimal.valueOf(100));
    } catch (MemberNotFoundException rejection) {
      System.err.println("Purchase rejected: " + rejection.getMessage());
    }
  }
}
