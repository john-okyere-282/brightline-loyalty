package com.brightline.loyalty.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class InMemoryMemberRepositoryTest {
  @Test
  void treatsNullAsAnAbsentId() {
    assertEquals(Optional.empty(), new InMemoryMemberRepository().findById(null));
  }
}
