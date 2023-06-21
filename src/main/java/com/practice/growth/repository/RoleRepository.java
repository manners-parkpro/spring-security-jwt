package com.practice.growth.repository;

import com.practice.growth.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<List<Role>> findByRoleNameIn(List<String> roleNames);
}
