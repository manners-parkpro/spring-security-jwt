package com.practice.growth.repository;

import com.practice.growth.domain.entity.Code;
import com.practice.growth.domain.types.YNType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeRepository extends JpaRepository<Code, Long> {

    List<Code> findByGroup_CodeIgnoreCaseAndActiveYn(String groupCode, YNType activeYn, Sort sort);

    List<Code> findByGroupIsNull(Sort sort);

    List<Code> findByGroupIsNullAndActiveYn(YNType activeYn, Sort sort);

    Optional<Code> findByCodeIgnoreCase(String code);

    Optional<Code> findByValueIgnoreCase(String value);
}
