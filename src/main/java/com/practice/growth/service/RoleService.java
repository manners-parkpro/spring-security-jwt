package com.practice.growth.service;

import com.practice.growth.domain.entity.Role;
import com.practice.growth.exception.NotFoundException;
import com.practice.growth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    /**
     * Role 목록
     *
     * @return
     */
    public List<Role> roleList() {
        Sort.by(Sort.Direction.ASC, "sortOrder");
        return roleRepository.findAll();
    }

    public Role findByRoleName(String roleId) throws NotFoundException {
        Optional<Role> findRole = roleRepository.findById(roleId);
        if (findRole.isPresent())
            return findRole.get();
        else
            throw new NotFoundException(NotFoundException.ROLE_NOT_FOUND);
    }
}
