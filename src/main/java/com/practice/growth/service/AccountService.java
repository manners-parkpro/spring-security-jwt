package com.practice.growth.service;

import com.practice.growth.domain.dto.AccountDto;
import com.practice.growth.domain.dto.AccountRoleDto;
import com.practice.growth.domain.entity.Account;
import com.practice.growth.domain.entity.AccountRole;
import com.practice.growth.domain.entity.Role;
import com.practice.growth.domain.types.ModifyType;
import com.practice.growth.domain.types.ProviderType;
import com.practice.growth.domain.types.RoleType;
import com.practice.growth.domain.types.YNType;
import com.practice.growth.exception.AlreadyEntity;
import com.practice.growth.exception.NotFoundException;
import com.practice.growth.exception.RequiredParamNonException;
import com.practice.growth.exception.UserNotFoundException;
import com.practice.growth.repository.AccountRepository;
import com.practice.growth.repository.AccountRoleRepository;
import com.practice.growth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;
    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public Account findByUsername(String username) throws NotFoundException {
        return repository.findByUsernameIgnoreCaseAndActiveYn(username, YNType.Y).orElseThrow(() -> new UserNotFoundException(username, UserNotFoundException.USER_NOT_FOUND));
    }

    public Account findByEmail(String email) throws NotFoundException {
        return repository.findByEmailIgnoreCaseAndActiveYn(email, YNType.Y).orElseThrow(() -> new UserNotFoundException(email, UserNotFoundException.USER_NOT_FOUND));
    }

    @Transactional
    public Long createUser(AccountDto dto) throws Exception {
        if (dto == null)
            throw new RequiredParamNonException("RequiredParamNonException");

        if (repository.countByUsernameIgnoreCase(dto.getUsername()) > 0 || repository.countByEmail(dto.getEmail()) > 0)
            throw new AlreadyEntity("AlreadyEntity");

        Account account = new Account();
        account.setEmail(dto.getEmail());
        account.setPassword(passwordEncoder.encode(dto.getPassword()));
        account.setUsername(dto.getUsername());

        if (StringUtils.isNotBlank(dto.getTel()))
            account.setTel(dto.getTel());

        account.setProvider(dto.getProvider() == null ? ProviderType.WEB : dto.getProvider());

        modifyUserRole(RoleType.ROLE_USER, account, ModifyType.Register);
        repository.save(account);

        return account.getId();
    }

    @Transactional
    void modifyLastLoginAtByAccount(Account account) {
        account.setLastLoginAt(LocalDateTime.now());
        repository.save(account);
    }
    @Transactional
    void modifyUserRole(RoleType roleType, Account account, ModifyType modifyType) throws NotFoundException {
        Optional<Role> optRole = Optional.of(roleRepository.findById(roleType.getDesc()).orElseThrow(() -> new NotFoundException("Role not found : " + roleType, NotFoundException.ROLE_NOT_FOUND)));
        if (optRole.isEmpty())
            throw new NotFoundException("Role not found : " + roleType, NotFoundException.ROLE_NOT_FOUND);

        String roleName = optRole.get().getRoleName();

        AccountRole accountRole;
        Optional<AccountRole> optionalAccountRole = accountRoleRepository.findByRoleName(roleName);
        if (optionalAccountRole.isPresent())
            accountRole = optionalAccountRole.get();
        else {
            accountRole = new AccountRole();
            accountRole.setRoleName(optRole.get().getRoleName());
            accountRole.setAccount(account);
            accountRole.setSortOrder(accountRoleRepository.getMaxSortOrder(account));
        }

        if (ModifyType.Register.equals(modifyType)) {
            if (!account.getAccountRoles().contains(accountRole)) {
                account.getAccountRoles().add(accountRole);
                repository.save(account);
            }
        } else {
            if (account.getAccountRoles().contains(accountRole)) {
                account.getAccountRoles().remove(accountRole);
                repository.save(account);
            }
        }
    }
}
