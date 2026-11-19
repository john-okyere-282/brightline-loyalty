package com.brightline.loyalty.service;

import com.brightline.loyalty.model.Member;
import com.brightline.loyalty.repository.MemberRepository;
import com.brightline.loyalty.repository.RepositoryAccessException;
import com.brightline.loyalty.rule.LoyaltyScheme;
import com.brightline.loyalty.rule.TierRule;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

public final class PointsCalculator {
  private static final int MAX_MEMBER_ID_LENGTH = 128;
  private final MemberRepository repository;
  private final LoyaltyScheme scheme;

  public PointsCalculator(MemberRepository repository, LoyaltyScheme scheme) {
    this.scheme = Objects.requireNonNull(scheme, "scheme");
    this.repository = Objects.requireNonNull(repository, "repository");
  }

  public int calculatePoints(String memberId, BigDecimal amount) {
    validateMemberId(memberId);
    validateAmount(amount);
    Member member = findMember(memberId);
    TierRule tier = scheme.findTier(member.tier()).orElseThrow(UnknownTierException::new);
    try {
      int basePoints = amount.divide(BigDecimal.TEN, 0, RoundingMode.DOWN).intValueExact();
      int tierPoints = tier.calculateTierPoints(basePoints);
      int bonusPoints = amount.compareTo(BigDecimal.valueOf(500)) > 0 ? 50 : 0;
      return Math.addExact(tierPoints, bonusPoints);
    } catch (ArithmeticException overflow) {
      throw new PointsOverflowException(overflow);
    }
  }

  private void validateAmount(BigDecimal amount) {
    if (amount == null) {
      throw new InvalidPurchaseException("Purchase amount is required.");
    }
    if (amount.signum() < 0) {
      throw new InvalidPurchaseException("Purchase amount cannot be negative.");
    }
  }


  private void validateMemberId(String memberId) {
    if (memberId == null || memberId.isBlank()) {
      throw new IllegalArgumentException("Member id is required.");
    }
    if (memberId.length() > MAX_MEMBER_ID_LENGTH) {
      throw new IllegalArgumentException("Member id must be at most 128 characters.");
    }
  }

  private Member findMember(String memberId) {
    Optional<Member> member;
    try {
      member = repository.findById(memberId);
    } catch (RepositoryAccessException failure) {
      throw new MemberLookupException("Member lookup could not be completed.", failure);
    }
    if (member == null) {
      throw new MemberLookupException("Member repository returned null instead of Optional.", null);
    }
    return member.orElseThrow(MemberNotFoundException::new);
  }
}
