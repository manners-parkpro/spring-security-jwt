package com.practice.growth.service;

import com.practice.growth.domain.dto.AccountDto;
import com.practice.growth.domain.entity.Account;
import com.practice.growth.exception.AlreadyEntity;
import com.practice.growth.exception.RequiredParamNonException;
import com.practice.growth.exception.UserNotFoundException;
import com.practice.growth.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    public Long createUser(AccountDto dto) throws Exception {
        if (dto == null)
            throw new RequiredParamNonException("RequiredParamNonException");

        if (repository.countAccountByEmail(dto.getEmail()) > 0)
            throw new AlreadyEntity("AlreadyEntity");

        Account account = new Account();
        account.setEmail(dto.getEmail());
        account.setPassword(passwordEncoder.encode(dto.getPassword()));
        account.setUsername(dto.getUsername());
        account.setTel(dto.getTel());
        account.setRole("ROLE_USER");

        repository.save(account);

        return account.getId();
    }
}
