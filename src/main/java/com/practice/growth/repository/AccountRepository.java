package com.practice.growth.repository;

import com.practice.growth.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    int countAccountByEmail(String email);
}
