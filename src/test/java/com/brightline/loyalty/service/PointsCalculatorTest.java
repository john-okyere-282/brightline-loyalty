package com.brightline.loyalty.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.brightline.loyalty.repository.InMemoryMemberRepository;
import com.brightline.loyalty.repository.MemberRepository;
import com.brightline.loyalty.repository.RepositoryAccessException;
import java.io.IOException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PointsCalculatorTest {
  @ParameterizedTest
  @CsvSource({
      "m1,100,10", "m2,250,50", "m3,200,30", "m1,0,0", "m1,9.99,0",
      "m3,19.99,1", "m1,499.99,49", "m1,500,50", "m1,500.01,100", "m2,600,170",
      "m1,21474835970,2147483647"
  })
  void calculatesActualTierPoints(String memberId, BigDecimal amount, int expected) {
    PointsCalculator calculator = new PointsCalculator(new InMemoryMemberRepository());
    assertEquals(expected, calculator.calculatePoints(memberId, amount));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" "})
  void rejectsMissingId(String memberId) {
    PointsCalculator calculator = new PointsCalculator(new InMemoryMemberRepository());
    IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
        () -> calculator.calculatePoints(memberId, BigDecimal.TEN));
    assertEquals("Member id is required.", failure.getMessage());
  }

  @Test
  void distinguishesMissingMembersFromZeroPoints() {
    PointsCalculator calculator = new PointsCalculator(new InMemoryMemberRepository());
    MemberNotFoundException failure = assertThrows(MemberNotFoundException.class,
        () -> calculator.calculatePoints("unknown", BigDecimal.TEN));
    assertEquals("Member was not found.", failure.getMessage());
  }

  @Test
  void preservesRepositoryFailureCause() throws RepositoryAccessException {
    MemberRepository repository = mock(MemberRepository.class);
    var outage = new RepositoryAccessException("Unavailable", new IOException("Offline"));
    when(repository.findById("m1")).thenThrow(outage);
    var calculator = new PointsCalculator(repository);

    var failure = assertThrows(MemberLookupException.class,
        () -> calculator.calculatePoints("m1", BigDecimal.TEN));
    assertSame(outage, failure.getCause());
  }

  @Test
  void reportsANullOptionalAsABrokenRepositoryContract() {
    var calculator = new PointsCalculator(memberId -> null);

    var failure = assertThrows(MemberLookupException.class,
        () -> calculator.calculatePoints("m1", BigDecimal.TEN));
    assertEquals("Member repository returned null instead of Optional.", failure.getMessage());
  }

  @Test
  void letsUnexpectedProgrammingErrorsPropagate() {
    var bug = new NullPointerException("Adapter bug");
    var calculator = new PointsCalculator(memberId -> {
      throw bug;
    });

    assertSame(bug, assertThrows(NullPointerException.class,
        () -> calculator.calculatePoints("m1", BigDecimal.TEN)));
  }

  @Test
  void rejectsLongIdsBeforeLookup() {
    var calculator = new PointsCalculator(memberId -> {
      throw new AssertionError("Invalid id must not reach the repository");
    });
    var failure = assertThrows(IllegalArgumentException.class,
        () -> calculator.calculatePoints("x".repeat(129), BigDecimal.TEN));
    assertEquals("Member id must be at most 128 characters.", failure.getMessage());
  }
  @ParameterizedTest
  @CsvSource(value = {
      "NULL,Purchase amount is required.",
      "-0.01,Purchase amount cannot be negative."
  }, nullValues = "NULL")
  void rejectsInvalidAmounts(BigDecimal amount, String message) {
    var calculator = new PointsCalculator(memberId -> {
      throw new AssertionError("Invalid amount must not reach the repository");
    });

    var failure = assertThrows(InvalidPurchaseException.class,
        () -> calculator.calculatePoints("m1", amount));
    assertEquals(message, failure.getMessage());
  }

  @ParameterizedTest
  @CsvSource({"m1,21474836480", "m2,10737418240", "m3,21474836470", "m1,21474835980"})
  void rejectsOverflowAtAnyCalculationStep(String memberId, BigDecimal amount) {
    var calculator = new PointsCalculator(new InMemoryMemberRepository());

    var failure = assertThrows(PointsOverflowException.class,
        () -> calculator.calculatePoints(memberId, amount));
    assertEquals("Calculated points exceed the supported integer range.", failure.getMessage());
  }
}
