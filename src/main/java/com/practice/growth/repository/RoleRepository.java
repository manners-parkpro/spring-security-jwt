package com.practice.growth.repository;

import com.practice.growth.domain.entity.Account;
import com.practice.growth.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<List<Role>> findByRoleNameIn(List<String> roleNames);
    Optional<Role> findByRoleName(String roleName);
}
