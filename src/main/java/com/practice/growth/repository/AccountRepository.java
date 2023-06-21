package com.practice.growth.repository;

import com.practice.growth.domain.entity.Account;
import com.practice.growth.domain.types.YNType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    int countByEmail(String email);

    int countByUsernameIgnoreCase(String username);
    Optional<Account> findByUsernameIgnoreCaseAndActiveYn(String username, YNType ynType);
    Optional<Account> findByEmailIgnoreCase(String email);
    Optional<Account> findByProviderId(String providerId);
}
