package com.brightline.loyalty.repository;

import com.brightline.loyalty.model.Member;
import java.util.Map;
import java.util.Optional;

public final class InMemoryMemberRepository implements MemberRepository {
  private final Map<String, Member> members = Map.of(
      "m1", new Member("m1", "Kofi Mensah", "STANDARD"),
      "m2", new Member("m2", "Ama Owusu", "GOLD"),
      "m3", new Member("m3", "Yaa Boateng", "SILVER"));

  @Override
  public Optional<Member> findById(String id) {
    return id == null ? Optional.empty() : Optional.ofNullable(members.get(id));
  }
}
