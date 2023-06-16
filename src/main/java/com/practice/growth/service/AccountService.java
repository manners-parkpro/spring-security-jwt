package com.practice.growth.service;

import com.practice.growth.domain.dto.AccountDto;
import com.practice.growth.domain.entity.Account;
import com.practice.growth.domain.types.ProviderType;
import com.practice.growth.exception.AlreadyEntity;
import com.practice.growth.exception.NotFoundException;
import com.practice.growth.exception.RequiredParamNonException;
import com.practice.growth.exception.UserNotFoundException;
import com.practice.growth.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    public Account findByUsername(String username) throws NotFoundException {
        return repository.findByUsernameIgnoreCase(username).orElseThrow(() -> new UserNotFoundException(username, UserNotFoundException.USER_NOT_FOUND));
    }

    public Account findByEmail(String email) throws NotFoundException {
        return repository.findByEmailIgnoreCase(email).orElseThrow(() -> new UserNotFoundException(email, UserNotFoundException.USER_NOT_FOUND));
    }

    @Transactional
    public Long createUser(AccountDto dto) throws Exception {
        if (dto == null)
            throw new RequiredParamNonException("RequiredParamNonException");

        if (repository.countByUsername(dto.getEmail()) > 0 || repository.countByEmail(dto.getUsername()) > 0)
            throw new AlreadyEntity("AlreadyEntity");

        Account account = new Account();
        account.setEmail(dto.getEmail());
        account.setPassword(passwordEncoder.encode(dto.getPassword()));
        account.setUsername(dto.getUsername());

        if (StringUtils.isNotBlank(dto.getTel()))
            account.setTel(dto.getTel());

        account.setRoles(StringUtils.isBlank(dto.getRoles()) ? "ROLE_USER" : dto.getRoles());
        account.setProvider(dto.getProvider() == null ? ProviderType.WEB : dto.getProvider());

        repository.save(account);

        return account.getId();
    }

    @Transactional
    public void modifyAccount(Account account) {
        account.setLastLoginAt(LocalDateTime.now());
        repository.save(account);
    }
}
