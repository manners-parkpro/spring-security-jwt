package com.practice.growth.filter;

import com.practice.growth.repository.AccountRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 인가 관련 JwtFilter
 */
@Log4j2
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final AccountRepository accountRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, AccountRepository accountRepository) {
        super(authenticationManager);
        this.accountRepository = accountRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("doFilter()");
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        log.info("JWT Filter destroy()");
        super.destroy();
    }
}
