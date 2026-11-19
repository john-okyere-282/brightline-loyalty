package com.brightline.loyalty.repository;

import com.brightline.loyalty.model.Member;
import java.util.Optional;

public interface MemberRepository {
  /**
   * Returns the matching member or an empty Optional when absent. Never returns null.
   *
   * @throws RepositoryAccessException when the data source cannot complete the lookup
   */
  Optional<Member> findById(String id) throws RepositoryAccessException;
}
