package com.practice.growth.service;

import com.practice.growth.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final AccountRepository accountRepository;
}
