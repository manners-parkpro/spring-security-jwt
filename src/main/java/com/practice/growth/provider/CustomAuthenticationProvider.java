package com.practice.growth.provider;

import com.practice.growth.domain.auth.PrincipalDetails;
import com.practice.growth.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    // return new UsernamePasswordAuthenticationToken = Authentication
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        log.info("CustomAuthenticationProvider authenticate()");

        String username = authentication.getName();
        String userPw = (String) authentication.getCredentials();
        PrincipalDetails userDetails = (PrincipalDetails) userDetailsService.loadUserByUsername(username);

        if (userDetails == null || !username.equals(userDetails.getUsername()) || !passwordEncoder.matches(userPw, userDetails.getPassword()))
            throw new BadCredentialsException(username); //아이디, 비밀번호 불일치
        else if(!userDetails.isEnabled())
            throw new DisabledException(username); //계정 비활성화
        else if(!userDetails.isAccountNonLocked())
            throw new LockedException(username); //계정 잠김
        else if (!userDetails.isAccountNonExpired())
            throw new AccountExpiredException(username); //계정 만료
        else if (!userDetails.isCredentialsNonExpired())
            throw new CredentialsExpiredException(username); //비밀번호 만료

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    //반환 객체 타입 검사
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
