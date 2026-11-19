package com.brightline.loyalty.service;

import com.brightline.loyalty.model.Member;
import com.brightline.loyalty.repository.MemberRepository;
import com.brightline.loyalty.repository.RepositoryAccessException;
import java.util.Objects;
import java.util.Optional;

public final class PointsCalculator {
  private static final int MAX_MEMBER_ID_LENGTH = 128;
  private final MemberRepository repository;

  public PointsCalculator(MemberRepository repository) {
    this.repository = Objects.requireNonNull(repository, "repository");
  }

  public int calculatePoints(String memberId, double amount) {
    validateMemberId(memberId);
    Member member = findMember(memberId);
    int points = (int) (amount / 10);
    if ("SILVER".equals(member.tier())) {
      points = (int) (points * 1.5);
    } else if ("GOLD".equals(member.tier())) {
      points *= 2;
    }
    if (amount > 500) {
      points += 50;
    }
    return points;
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
