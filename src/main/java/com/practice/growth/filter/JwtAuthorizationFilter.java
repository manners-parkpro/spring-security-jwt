package com.practice.growth.filter;

import com.practice.growth.provider.JwtProvider;
import com.practice.growth.repository.AccountRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 인가 Filter
 * BasicAuthenticationFilter 권한이나 인증이 필요한 특정 주소를 요청했을 때 해당 필터는 무조건 호출하게 된다.
 */
@Log4j2
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private final JwtProvider jwtProvider;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        super(authenticationManager);
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("doFilter()");

        String accessToken = jwtProvider.resolveToken(request);
        if (StringUtils.isNotBlank(accessToken)) {
            Authentication authentication = jwtProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        log.info("JWT Filter destroy()");
        super.destroy();
    }


}
