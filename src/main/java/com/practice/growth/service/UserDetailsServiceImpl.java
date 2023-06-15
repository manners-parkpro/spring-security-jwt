package com.practice.growth.service;

import com.practice.growth.authentication.PrincipalDetails;
import com.practice.growth.domain.entity.Account;
import com.practice.growth.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Security 설정에서 loginProcessingUrl(login-process)
// login-process 해당 URL 요청이 오면 자동으로 UserDetailsService Type으로 IOC되어 있는 loadUserByUsername 함수가 실행

@Log4j2
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountService service;

    // security session = Authentication = UserDetails
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account;

        try {
            account = service.findByUsername(username);
        } catch (NotFoundException e) {
            String msg = "User Not found : " + username;
            log.error(msg);
            throw new UsernameNotFoundException(msg);
        }

        // LastLoginAt modify
        service.modifyAccount(account);

        return new PrincipalDetails(account);
    }
}
