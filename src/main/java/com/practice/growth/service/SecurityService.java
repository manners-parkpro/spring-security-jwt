package com.practice.growth.service;

import com.practice.growth.domain.entity.Account;
import com.practice.growth.domain.types.YNType;
import com.practice.growth.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final AccountRepository accountRepository;

    /**
     * 로그인 사용자 갖고오기
     *
     * @return
     */
    public Account getLoginUser() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) return null;

        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails)
            username = ((UserDetails) principal).getUsername();
        else
            username = principal.toString();

        Optional<Account> findUser = accountRepository.findByUsernameIgnoreCaseAndActiveYn(username, YNType.Y);
        if (findUser.isEmpty())
            return null;

        return findUser.get();
    }
}
