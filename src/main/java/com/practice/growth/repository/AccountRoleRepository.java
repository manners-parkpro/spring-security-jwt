package com.practice.growth.repository;

import com.practice.growth.domain.entity.Account;
import com.practice.growth.domain.entity.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRoleRepository extends JpaRepository<AccountRole, Long> {

    Optional<List<AccountRole>> findByRoleNameIn(List<String> roleNames);
    Optional<AccountRole> findByRoleName(String roleName);

    @Query(value = "SELECT max(sortOrder) FROM AccountRole WHERE Account = :account")
    int getMaxSortOrder(Account account);
}
